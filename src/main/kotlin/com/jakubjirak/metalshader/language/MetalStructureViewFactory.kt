package com.jakubjirak.metalshader.language

import com.intellij.ide.structureView.*
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.lang.PsiStructureViewFactory
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import javax.swing.Icon

class MetalStructureViewFactory : PsiStructureViewFactory {
    override fun getStructureViewBuilder(psiFile: PsiFile): StructureViewBuilder {
        return object : TreeBasedStructureViewBuilder() {
            override fun createStructureViewModel(editor: Editor?): StructureViewModel {
                return MetalStructureViewModel(psiFile)
            }
        }
    }
}

class MetalStructureViewModel(psiFile: PsiFile) : 
    StructureViewModelBase(psiFile, MetalStructureViewElement(psiFile)),
    StructureViewModel.ElementInfoProvider {
    
    override fun isAlwaysShowsPlus(element: StructureViewTreeElement): Boolean = false
    override fun isAlwaysLeaf(element: StructureViewTreeElement): Boolean = false
}

class MetalStructureViewElement(private val element: PsiElement) : StructureViewTreeElement {
    override fun getValue(): Any = element
    
    override fun navigate(requestFocus: Boolean) {
        // Navigation implementation
    }
    
    override fun canNavigate(): Boolean = false
    override fun canNavigateToSource(): Boolean = false
    
    override fun getPresentation(): ItemPresentation {
        return object : ItemPresentation {
            override fun getPresentableText(): String = presentableText(element.text) ?: element.toString()

            override fun getLocationString(): String? = null
            override fun getIcon(unused: Boolean): Icon? = MetalIcons.FILE
        }
    }
    
    override fun getChildren(): Array<TreeElement> {
        if (element !is PsiFile) return emptyArray()
        
        val children = mutableListOf<TreeElement>()
        element.children.forEach { child ->
            val text = child.text.trim()
            if (STRUCTURE_PREFIXES.any { text.startsWith(it) }) {
                children.add(MetalStructureViewElement(child))
            }
        }
        return children.toTypedArray()
    }
}

private val STRUCTURE_PREFIXES = listOf("kernel", "vertex", "fragment", "struct")

private val NAMED_ELEMENTS = listOf(
    Triple("kernel", Regex("""kernel\s+\w+\s+(\w+)"""), "⚡"),
    Triple("vertex", Regex("""vertex\s+\w+\s+(\w+)"""), "🔺"),
    Triple("fragment", Regex("""fragment\s+\w+\s+(\w+)"""), "🎨"),
    Triple("struct", Regex("""struct\s+(\w+)"""), "📦"),
)

/** Structure-view label for a top-level element's text, or null when it is not a named shader element. */
internal fun presentableText(text: String): String? {
    val named = NAMED_ELEMENTS.firstOrNull { (keyword, _, _) -> text.contains(keyword) }
    return when {
        named != null -> "${named.third} ${named.second.find(text)?.groupValues?.get(1) ?: named.first}"
        text.startsWith("//") -> "💬 Comment"
        else -> null
    }
}
