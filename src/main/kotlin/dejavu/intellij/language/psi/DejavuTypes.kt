package dejavu.intellij.language.psi

import com.intellij.psi.tree.IFileElementType
import dejavu.intellij.language.DejavuLanguage

object DejavuTypes {
    val FILE = IFileElementType(DejavuLanguage)
    val TEMPLATE = DejavuElementType("TEMPLATE")
    val EXPR = DejavuElementType("EXPR")
    val STATEMENT = DejavuElementType("STATEMENT")
    val COMMENT = DejavuElementType("COMMENT")
    val TEXT_LITERAL = DejavuElementType("TEXT_LITERAL")
    val IF_STATEMENT = DejavuElementType("IF_STATEMENT")
    val FOR_STATEMENT = DejavuElementType("FOR_STATEMENT")
    val END_STATEMENT = DejavuElementType("END_STATEMENT")

    // Tokens - Slot Start with whitespace control
    val SLOT_START = DejavuTokenType("SLOT_START")
    val SLOT_START_OUTPUT = DejavuTokenType("SLOT_START_OUTPUT")           // <%=
    val SLOT_START_TRIM_BEFORE = DejavuTokenType("SLOT_START_TRIM_BEFORE") // <%_
    val SLOT_START_TRIM_AFTER = DejavuTokenType("SLOT_START_TRIM_AFTER")   // <%-
    val SLOT_START_TRIM_BOTH = DejavuTokenType("SLOT_START_TRIM_BOTH")     // <%~

    // Tokens - Slot End with whitespace control
    val SLOT_END = DejavuTokenType("SLOT_END")
    val SLOT_END_TRIM_BEFORE = DejavuTokenType("SLOT_END_TRIM_BEFORE")     // _%>
    val SLOT_END_TRIM_AFTER = DejavuTokenType("SLOT_END_TRIM_AFTER")       // -%>
    val SLOT_END_TRIM_BOTH = DejavuTokenType("SLOT_END_TRIM_BOTH")         // ~%>

    // Tokens - Comment
    val COMMENT_START = DejavuTokenType("COMMENT_START")
    val COMMENT_END = DejavuTokenType("COMMENT_END")
    val TEXT = DejavuTokenType("TEXT")
    val SLOT_CONTENT = DejavuTokenType("SLOT_CONTENT")
    val COMMENT_CONTENT = DejavuTokenType("COMMENT_CONTENT")
}