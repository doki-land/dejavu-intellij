package dejavu.intellij.language.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement

object DejavuFactory {
    fun createElement(node: ASTNode): PsiElement {
        return when (node.elementType) {
            DejavuTypes.TEMPLATE -> DejavuTemplateElement(node)
            DejavuTypes.EXPR -> DejavuExprElement(node)
            DejavuTypes.STATEMENT -> DejavuStatementElement(node)
            DejavuTypes.COMMENT -> DejavuCommentElement(node)
            DejavuTypes.TEXT_LITERAL -> DejavuTextLiteralElement(node)
            else -> DejavuElement(node)
        }
    }
}
