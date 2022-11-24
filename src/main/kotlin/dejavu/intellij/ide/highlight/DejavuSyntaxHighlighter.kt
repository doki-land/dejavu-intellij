package dejavu.intellij.ide.highlight

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase.pack
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import dejavu.intellij.language.DejavuLanguage
import dejavu.intellij.language.parser.DejavuLexer
import dejavu.intellij.language.psi.DejavuTypes
import dejavu.intellij.ide.highlight.HighlightColor as Color

class DejavuSyntaxHighlighter : SyntaxHighlighter {
    override fun getHighlightingLexer(): Lexer {
        return DejavuLexer(DejavuLanguage.LanguageConfig)
    }

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        return pack(getTokenColor(tokenType)?.textAttributesKey)
    }

    private fun getTokenColor(tokenType: IElementType): Color? {
        return when (tokenType) {
            DejavuTypes.SLOT_START, DejavuTypes.SLOT_END -> Color.KEYWORD
            DejavuTypes.COMMENT_START, DejavuTypes.COMMENT_END -> Color.COMMENT_BLOCK
            DejavuTypes.TEXT -> null
            DejavuTypes.SLOT_CONTENT -> Color.IDENTIFIER
            DejavuTypes.COMMENT_CONTENT -> Color.COMMENT_BLOCK
            TokenType.BAD_CHARACTER -> Color.BAD_CHARACTER
            else -> null
        }
    }
}
