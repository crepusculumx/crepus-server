package icu.crepusculumx.crepusserver.data

import kotlinx.serialization.Serializable

@JvmRecord
@Serializable
data class BlogFileInfo(val urlPath: String, val themeType: String, val fileType: String)

@JvmRecord
@Serializable
data class BlogInfo(val path: String, val title: String, val blogFileInfos: ArrayList<BlogFileInfo>)

@JvmRecord
@Serializable
data class BlogTreeNode(val path: String, val title: String, val isFolder: Boolean, val children: BlogTreeData?)


typealias BlogTreeData = ArrayList<BlogTreeNode>

@JvmRecord
data class BlogNamespace(val name: String)

typealias BlogNamespaces = ArrayList<BlogNamespace>