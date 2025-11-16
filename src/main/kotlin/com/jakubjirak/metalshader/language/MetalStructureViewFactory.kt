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
            override fun getPresentableText(): String {
                val text = element.text
                
                // Extract function names
                when {
                    text.contains("kernel") -> {
                        val match = Regex("""kernel\s+\w+\s+(\w+)""").find(text)
                        return "⚡ ${match?.groupValues?.get(1) ?: "kernel"}"
                    }
                    text.contains("vertex") -> {
                        val match = Regex("""vertex\s+\w+\s+(\w+)""").find(text)
                        return "🔺 ${match?.groupValues?.get(1) ?: "vertex"}"
                    }
                    text.contains("fragment") -> {
                        val match = Regex("""fragment\s+\w+\s+(\w+)""").find(text)
                        return "🎨 ${match?.groupValues?.get(1) ?: "fragment"}"
                    }
                    text.contains("struct") -> {
                        val match = Regex("""struct\s+(\w+)""").find(text)
                        return "📦 ${match?.groupValues?.get(1) ?: "struct"}"
                    }
                    text.startsWith("//") -> return "💬 Comment"
                    else -> return element.toString()
                }
            }
            
            override fun getLocationString(): String? = null
            override fun getIcon(unused: Boolean): Icon? = MetalIcons.FILE
        }
    }
    
    override fun getChildren(): Array<TreeElement> {
        if (element !is PsiFile) return emptyArray()
        
        val children = mutableListOf<TreeElement>()
        element.children.forEach { child ->
            val text = child.text.trim()
            if (text.startsWith("kernel") || text.startsWith("vertex") || 
                text.startsWith("fragment") || text.startsWith("struct")) {
                children.add(MetalStructureViewElement(child))
            }
        }
        return children.toTypedArray()
    }
}
