package dejavu.intellij.ide.highlight

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import dejavu.intellij.language.DokiLanguage
import dejavu.intellij.language.parser.DejavuLexer
import dejavu.intellij.language.psi.DejavuTypes

class DokiSyntaxHighlighter : SyntaxHighlighter {
    override fun getHighlightingLexer(): Lexer {
        return DejavuLexer(DokiLanguage.LanguageConfig)
    }

    override fun getTokenHighlights(tokenType: IElementType): Array<out TextAttributesKey?> {
        return SyntaxHighlighterBase.pack(getTokenColor(tokenType)?.textAttributesKey)
    }

    private fun getTokenColor(tokenType: IElementType): HighlightColor? {
        return when (tokenType) {
            DejavuTypes.SLOT_START, DejavuTypes.SLOT_END -> HighlightColor.KEYWORD
            DejavuTypes.COMMENT_START, DejavuTypes.COMMENT_END -> HighlightColor.COMMENT_BLOCK
            DejavuTypes.TEXT -> null
            DejavuTypes.SLOT_CONTENT -> HighlightColor.IDENTIFIER
            DejavuTypes.COMMENT_CONTENT -> HighlightColor.COMMENT_BLOCK
            TokenType.BAD_CHARACTER -> HighlightColor.BAD_CHARACTER
            else -> null
        }
    }
}