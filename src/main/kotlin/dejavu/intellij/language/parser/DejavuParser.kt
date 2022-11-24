package dejavu.intellij.language.parser

import com.intellij.lang.ASTNode
import com.intellij.lang.LightPsiParser
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiParser
import com.intellij.psi.tree.IElementType
import dejavu.intellij.language.TemplateConfig
import dejavu.intellij.language.psi.DejavuTypes

/**
 * Dejavu 模板语言解析器
 * 支持多种空白控制符的解析
 */
class DejavuParser(private val config: TemplateConfig) : PsiParser, LightPsiParser {
    override fun parse(root: IElementType, builder: PsiBuilder): ASTNode {
        parseLight(root, builder)
        return builder.treeBuilt
    }

    override fun parseLight(root: IElementType?, builder: PsiBuilder?) {
        if (builder == null) return

        val rootMarker = builder.mark()
        parseTemplate(builder)
        rootMarker.done(root ?: DejavuTypes.FILE)
    }

    private fun parseTemplate(builder: PsiBuilder) {
        val templateMarker = builder.mark()
        while (!builder.eof()) {
            when (builder.tokenType) {
                // 所有类型的 slot 开始定界符
                DejavuTypes.SLOT_START,
                DejavuTypes.SLOT_START_OUTPUT,
                DejavuTypes.SLOT_START_TRIM_BEFORE,
                DejavuTypes.SLOT_START_TRIM_AFTER,
                DejavuTypes.SLOT_START_TRIM_BOTH -> parseExpressionOrStatement(builder)
                DejavuTypes.COMMENT_START -> parseComment(builder)
                DejavuTypes.TEXT -> parseText(builder)
                else -> builder.advanceLexer()
            }
        }
        templateMarker.done(DejavuTypes.TEMPLATE)
    }

    private fun parseExpressionOrStatement(builder: PsiBuilder) {
        val exprMarker = builder.mark()
        val startTokenType = builder.tokenType
        builder.advanceLexer() // consume SLOT_START (any type)

        if (builder.tokenType == DejavuTypes.SLOT_CONTENT) {
            val content = builder.tokenText ?: ""
            val trimmedContent = content.trim()
            when {
                trimmedContent.startsWith("if") -> {
                    // 验证 if 语句语法：if [condition]，不允许 (condition) { 格式
                    if (!trimmedContent.matches(Regex("""^if\s+[^(]+$"""))) {
                        // 错误的 if 语法
                        exprMarker.done(DejavuTypes.EXPR)
                    } else {
                        parseIfStatement(builder, exprMarker)
                    }
                }
                trimmedContent.startsWith("loop") -> parseLoopStatement(builder, exprMarker)
                trimmedContent.startsWith("end") -> {
                    // 验证 end 语句语法：end [statement]，不允许 } 格式
                    if (!trimmedContent.matches(Regex("""^end\s+\w+"""))) {
                        // 错误的 end 语法
                        exprMarker.done(DejavuTypes.EXPR)
                    } else {
                        parseEndStatement(builder, exprMarker)
                    }
                }
                else -> parseExpression(builder, exprMarker)
            }
        } else if (isSlotEndToken(builder.tokenType)) {
            // Empty expression: <% %>, <%= %>, etc.
            exprMarker.done(DejavuTypes.EXPR)
            builder.advanceLexer() // consume SLOT_END (any type)
        } else {
            // Unexpected token, just complete the expression and consume the token
            exprMarker.done(DejavuTypes.EXPR)
            if (!builder.eof()) {
                builder.advanceLexer() // consume the unexpected token
            }
        }
    }

    private fun parseExpression(builder: PsiBuilder, marker: PsiBuilder.Marker) {
        if (builder.tokenType == DejavuTypes.SLOT_CONTENT) {
            builder.advanceLexer() // consume SLOT_CONTENT
        }
        // 消费任何类型的结束定界符
        if (isSlotEndToken(builder.tokenType)) {
            builder.advanceLexer()
        }
        marker.done(DejavuTypes.EXPR)
    }

    private fun parseIfStatement(builder: PsiBuilder, marker: PsiBuilder.Marker) {
        if (builder.tokenType == DejavuTypes.SLOT_CONTENT) {
            builder.advanceLexer() // consume SLOT_CONTENT
        }
        // 消费任何类型的结束定界符
        if (isSlotEndToken(builder.tokenType)) {
            builder.advanceLexer()
        }
        marker.done(DejavuTypes.STATEMENT)
    }

    private fun parseLoopStatement(builder: PsiBuilder, marker: PsiBuilder.Marker) {
        if (builder.tokenType == DejavuTypes.SLOT_CONTENT) {
            builder.advanceLexer() // consume SLOT_CONTENT
        }
        // 消费任何类型的结束定界符
        if (isSlotEndToken(builder.tokenType)) {
            builder.advanceLexer()
        }
        marker.done(DejavuTypes.STATEMENT)
    }

    private fun parseEndStatement(builder: PsiBuilder, marker: PsiBuilder.Marker) {
        if (builder.tokenType == DejavuTypes.SLOT_CONTENT) {
            builder.advanceLexer() // consume SLOT_CONTENT
        }
        // 消费任何类型的结束定界符
        if (isSlotEndToken(builder.tokenType)) {
            builder.advanceLexer()
        }
        marker.done(DejavuTypes.STATEMENT)
    }

    private fun parseComment(builder: PsiBuilder) {
        val commentMarker = builder.mark()
        builder.advanceLexer() // consume COMMENT_START

        if (builder.tokenType == DejavuTypes.COMMENT_CONTENT) {
            builder.advanceLexer() // consume COMMENT_CONTENT
        }

        if (builder.tokenType == DejavuTypes.COMMENT_END) {
            builder.advanceLexer() // consume COMMENT_END
        }

        commentMarker.done(DejavuTypes.COMMENT)
    }

    private fun parseText(builder: PsiBuilder) {
        val textMarker = builder.mark()
        builder.advanceLexer() // consume TEXT
        textMarker.done(DejavuTypes.TEXT_LITERAL)
    }

    /**
     * 检查 token 类型是否是 slot 结束定界符（包括各种空白控制符）
     */
    private fun isSlotEndToken(tokenType: IElementType?): Boolean {
        return tokenType == DejavuTypes.SLOT_END ||
               tokenType == DejavuTypes.SLOT_END_TRIM_BEFORE ||
               tokenType == DejavuTypes.SLOT_END_TRIM_AFTER ||
               tokenType == DejavuTypes.SLOT_END_TRIM_BOTH
    }
}
