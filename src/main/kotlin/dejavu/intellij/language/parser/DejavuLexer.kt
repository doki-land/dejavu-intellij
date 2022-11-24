package dejavu.intellij.language.parser

import com.intellij.lexer.Lexer
import com.intellij.lexer.LexerPosition
import com.intellij.psi.tree.IElementType
import dejavu.intellij.language.TemplateConfig
import dejavu.intellij.language.psi.DejavuTypes

/**
 * Dejavu 模板语言词法分析器
 * 支持多种空白控制符：
 * - <%= : 输出表达式
 * - <%_ : 去除前面空白
 * - <%- : 去除后面空白
 * - <%~ : 去除前后空白
 * - _%> : 去除前面空白（结束）
 * - -%> : 去除后面空白（结束）
 * - ~%> : 去除前后空白（结束）
 */
class DejavuLexer(private val config: TemplateConfig) : Lexer() {
    private lateinit var buffer: CharSequence
    private var startOffset: Int = 0
    private var endOffset: Int = 0
    private var currentOffset: Int = 0
    private var tokenStart: Int = 0
    private var tokenEnd: Int = 0
    private var currentTokenType: IElementType? = null
    private var state: Int = 0

    companion object {
        private const val STATE_NORMAL = 0
        private const val STATE_EXPR = 1
        private const val STATE_COMMENT = 2
    }

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.buffer = buffer
        this.startOffset = startOffset
        this.endOffset = endOffset
        this.currentOffset = startOffset
        this.tokenStart = startOffset
        this.tokenEnd = startOffset
        this.currentTokenType = null
        this.state = initialState
        advance()
    }

    override fun getState(): Int = state

    override fun getTokenType(): IElementType? = currentTokenType

    override fun getTokenStart(): Int = tokenStart

    override fun getTokenEnd(): Int = tokenEnd

    override fun advance() {
        tokenStart = currentOffset
        currentTokenType = null

        if (currentOffset >= endOffset) {
            currentTokenType = null
            tokenEnd = endOffset
            state = STATE_NORMAL
            return
        }

        when (state) {
            STATE_NORMAL -> advanceNormal()
            STATE_EXPR -> advanceExpr()
            STATE_COMMENT -> advanceComment()
        }
    }

    private fun advanceNormal() {
        // 尝试匹配各种开始定界符（按长度优先）
        val slotStartResult = tryParseSlotStart()
        if (slotStartResult != null) {
            currentTokenType = slotStartResult.first
            currentOffset += slotStartResult.second.length
            tokenEnd = currentOffset
            state = STATE_EXPR
            return
        }

        if (tryParseDelimiter(config.commentStart, DejavuTypes.COMMENT_START)) {
            state = STATE_COMMENT
            return
        }

        parseText()
    }

    private fun advanceExpr() {
        // 尝试匹配各种结束定界符（按长度优先）
        val slotEndResult = tryParseSlotEnd()
        if (slotEndResult != null) {
            currentTokenType = slotEndResult.first
            currentOffset += slotEndResult.second.length
            tokenEnd = currentOffset
            state = STATE_NORMAL
            return
        }

        parseExprContent()
    }

    private fun advanceComment() {
        if (tryParseDelimiter(config.commentEnd, DejavuTypes.COMMENT_END)) {
            state = STATE_NORMAL
            return
        }

        parseCommentContent()
    }

    /**
     * 尝试匹配各种开始定界符
     * @return 匹配到的 Token 类型和定界符字符串，如果没有匹配则返回 null
     */
    private fun tryParseSlotStart(): Pair<IElementType, String>? {
        // 基于 config.slotStart 构建所有可能的开始定界符
        val slotStart = config.slotStart
        if (slotStart.isEmpty()) return null
        
        val possibleStarts = listOf(
            slotStart + "~",  // 去除前后空白
            slotStart + "=",  // 输出表达式
            slotStart + "_",  // 去除前面空白
            slotStart + "-",  // 去除后面空白
            slotStart         // 基本
        )
        
        for (delimiter in possibleStarts) {
            if (delimiter.isEmpty()) continue
            if (currentOffset + delimiter.length <= endOffset &&
                buffer.substring(currentOffset, currentOffset + delimiter.length) == delimiter) {
                return when (delimiter) {
                    slotStart + "=" -> Pair(DejavuTypes.SLOT_START_OUTPUT, delimiter)
                    slotStart + "_" -> Pair(DejavuTypes.SLOT_START_TRIM_BEFORE, delimiter)
                    slotStart + "-" -> Pair(DejavuTypes.SLOT_START_TRIM_AFTER, delimiter)
                    slotStart + "~" -> Pair(DejavuTypes.SLOT_START_TRIM_BOTH, delimiter)
                    slotStart -> Pair(DejavuTypes.SLOT_START, delimiter)
                    else -> null
                }
            }
        }
        return null
    }

    /**
     * 尝试匹配各种结束定界符
     * @return 匹配到的 Token 类型和定界符字符串，如果没有匹配则返回 null
     */
    private fun tryParseSlotEnd(): Pair<IElementType, String>? {
        // 基于 config.slotEnd 构建所有可能的结束定界符
        val slotEnd = config.slotEnd
        if (slotEnd.isEmpty()) return null
        
        val possibleEnds = listOf(
            "~" + slotEnd,  // 去除前后空白
            "_" + slotEnd,  // 去除前面空白
            "-" + slotEnd,  // 去除后面空白
            slotEnd         // 基本
        )
        
        for (delimiter in possibleEnds) {
            if (delimiter.isEmpty()) continue
            if (currentOffset + delimiter.length <= endOffset &&
                buffer.substring(currentOffset, currentOffset + delimiter.length) == delimiter) {
                return when (delimiter) {
                    "_" + slotEnd -> Pair(DejavuTypes.SLOT_END_TRIM_BEFORE, delimiter)
                    "-" + slotEnd -> Pair(DejavuTypes.SLOT_END_TRIM_AFTER, delimiter)
                    "~" + slotEnd -> Pair(DejavuTypes.SLOT_END_TRIM_BOTH, delimiter)
                    slotEnd -> Pair(DejavuTypes.SLOT_END, delimiter)
                    else -> null
                }
            }
        }
        return null
    }

    private fun tryParseDelimiter(delimiter: String, tokenType: IElementType): Boolean {
        if (delimiter.isEmpty()) return false
        if (currentOffset + delimiter.length <= endOffset && 
            buffer.substring(currentOffset, currentOffset + delimiter.length) == delimiter) {
            currentTokenType = tokenType
            currentOffset += delimiter.length
            tokenEnd = currentOffset
            return true
        }
        return false
    }

    private fun parseExprContent() {
        currentTokenType = DejavuTypes.SLOT_CONTENT
        // 基于 config.slotEnd 构建所有可能的结束定界符
        val slotEnd = config.slotEnd
        val possibleEnds = listOf(
            "~" + slotEnd,  // 去除前后空白
            "_" + slotEnd,  // 去除前面空白
            "-" + slotEnd,  // 去除后面空白
            slotEnd         // 基本
        )
        while (currentOffset < endOffset) {
            // 检查是否匹配任何结束定界符
            for (delimiter in possibleEnds) {
                if (delimiter.isEmpty()) continue
                if (currentOffset + delimiter.length <= endOffset &&
                    buffer.substring(currentOffset, currentOffset + delimiter.length) == delimiter) {
                    tokenEnd = currentOffset
                    return
                }
            }
            currentOffset++
        }
        tokenEnd = currentOffset
    }

    private fun parseCommentContent() {
        currentTokenType = DejavuTypes.COMMENT_CONTENT
        while (currentOffset < endOffset) {
            if (config.commentEnd.isNotEmpty() &&
                currentOffset + config.commentEnd.length <= endOffset && 
                buffer.substring(currentOffset, currentOffset + config.commentEnd.length) == config.commentEnd) {
                break
            }
            currentOffset++
        }
        tokenEnd = currentOffset
    }

    private fun parseText() {
        currentTokenType = DejavuTypes.TEXT
        val start = currentOffset
        // 基于 config.slotStart 构建所有可能的开始定界符
        val slotStart = config.slotStart
        val possibleStarts = if (slotStart.isNotEmpty()) {
            listOf(
                slotStart + "~",  // 去除前后空白
                slotStart + "=",  // 输出表达式
                slotStart + "_",  // 去除前面空白
                slotStart + "-",  // 去除后面空白
                slotStart         // 基本
            )
        } else {
            emptyList()
        }
        while (currentOffset < endOffset) {
            // 检查是否匹配任何开始定界符
            for (delimiter in possibleStarts) {
                if (delimiter.isEmpty()) continue
                if (currentOffset + delimiter.length <= endOffset &&
                    buffer.substring(currentOffset, currentOffset + delimiter.length) == delimiter) {
                    tokenEnd = currentOffset
                    return
                }
            }
            // 检查是否匹配注释开始定界符
            if (config.commentStart.isNotEmpty() &&
                currentOffset + config.commentStart.length <= endOffset &&
                buffer.substring(currentOffset, currentOffset + config.commentStart.length) == config.commentStart) {
                tokenEnd = currentOffset
                return
            }
            currentOffset++
        }
        tokenEnd = currentOffset
    }

    override fun getCurrentPosition(): LexerPosition {
        return object : LexerPosition {
            override fun getOffset(): Int = currentOffset
            override fun getState(): Int = getState()
        }
    }

    override fun restore(position: LexerPosition) {
        currentOffset = position.offset
        state = position.state
        tokenStart = currentOffset
        tokenEnd = currentOffset
        currentTokenType = null
        advance()
    }

    override fun getBufferSequence(): CharSequence = buffer

    override fun getBufferEnd(): Int = endOffset
}
