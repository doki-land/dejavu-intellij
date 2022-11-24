package dejavu.intellij.ide

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler
import dejavu.intellij.language.psi.DejavuTypes

class YggQuoteHandler : SimpleTokenSetQuoteHandler(DejavuTypes.SLOT_CONTENT)
