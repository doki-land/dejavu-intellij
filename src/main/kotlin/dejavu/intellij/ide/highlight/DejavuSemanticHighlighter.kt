package dejavu.intellij.ide.highlight

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.codeInsight.daemon.impl.HighlightVisitor
import com.intellij.codeInsight.daemon.impl.analysis.HighlightInfoHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import dejavu.intellij.language.file.DejavuFileNode
import dejavu.intellij.language.psi.DejavuVisitor

class DejavuSemanticHighlighter : DejavuVisitor(), HighlightVisitor {
    private var infoHolder: HighlightInfoHolder? = null

    override fun clone(): HighlightVisitor = DejavuSemanticHighlighter()

    override fun suitableForFile(file: PsiFile): Boolean = file is DejavuFileNode

    override fun visit(element: PsiElement) = element.accept(this)

    override fun analyze(file: PsiFile, whole: Boolean, holder: HighlightInfoHolder, action: Runnable): Boolean {
        infoHolder = holder
        action.run()
        return true
    }

    private fun highlight(element: PsiElement, color: HighlightColor) {
        val builder = HighlightInfo.newHighlightInfo(HighlightInfoType.INFORMATION)
        builder.textAttributes(color.textAttributesKey)
        builder.range(element)
        infoHolder?.add(builder.create())
    }
}