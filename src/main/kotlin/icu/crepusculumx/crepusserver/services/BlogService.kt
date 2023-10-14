package icu.crepusculumx.crepusserver.services

import icu.crepusculumx.crepusserver.annotations.WebSocketRoute
import icu.crepusculumx.crepusserver.data.*
import icu.crepusculumx.crepusserver.websockets.messages.Message
import icu.crepusculumx.crepusserver.websockets.Sid
import icu.crepusculumx.crepusserver.websockets.WebSocketServer
import icu.crepusculumx.crepusserver.websockets.messages.BlogInfoReq
import org.springframework.stereotype.Service

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListSet
import java.util.concurrent.LinkedBlockingQueue

@Service
class BlogService {

    val blogProviders = ConcurrentSkipListSet<Sid>()

    val blogTreeDataMap = ConcurrentHashMap<Sid, BlogTreeData>()

    fun getBlogTreeData(): BlogTreeData {
        val blogTreeData = BlogTreeData()

        blogProviders.forEach() { sid ->
            blogTreeDataMap[sid] = getBlogTreeDataBySid(sid)
        }

        blogTreeDataMap.forEach() { item ->
            blogTreeData.addAll(item.value)
        }

        return blogTreeData
    }

    fun getBlogTreeDataByUserName(user: String): BlogTreeData {
        val blogTreeData = getBlogTreeData()
        for (userBlogTreeData in blogTreeData) {
            if (userBlogTreeData.title == user && userBlogTreeData.isFolder && userBlogTreeData.children != null) {
                return userBlogTreeData.children
            }
        }
        return BlogTreeData()
    }

    @WebSocketRoute("/blog/set-blog-tree-data")
    fun setBlogTreeData(recMsg: Message) {
        val recRawMsg = recMsg.getRawMessage<BlogTreeData>()
        blogTreeDataMap[recMsg.sid] = recRawMsg.data
    }

    @WebSocketRoute("/blog/set-blog-tree-xxxx")
    fun setBlogTreeDataxxx(recMsg: Message) {
        val recRawMsg = recMsg.getRawMessage<BlogTreeData>()
        blogTreeDataMap[recMsg.sid] = recRawMsg.data
    }

    @WebSocketRoute("/blog/set-blog-provider")
    fun setBlogProvider(recMsg: Message) {
        blogProviders.add(recMsg.sid)
    }

    fun removeSocketById(sid: Sid) {
        blogProviders.remove(sid)
    }

    /**
     * Blocking
     */
    fun getBlogTreeDataBySid(sid: Sid): BlogTreeData {
        val reply = WebSocketServer.sendMessageByIdWithReply<String, BlogTreeData>(sid, "/get-blog-tree-data", "")
        val res = reply.getReply().getRawMessage<BlogTreeData>().data
        reply.endReply()
        return res
    }

    /**
     * Blocking
     */
    fun getBlogInfoBySid(sid: Sid, path: String): BlogInfo {
        val reply =
            WebSocketServer.sendMessageByIdWithReply<BlogInfoReq, BlogInfo>(sid, "/get-blog-info", BlogInfoReq(path))
        val res = reply.getReply().getRawMessage<BlogInfo>().data
        reply.endReply()
        return res
    }

    fun getBlogInfo(path: String): BlogInfo {
        val pathParts = path.split("/")
        val blogName = pathParts[pathParts.lastIndex]
        val res = BlogInfo(path, blogName, ArrayList())
        blogProviders.forEach() { sid ->
            val blogInfo = getBlogInfoBySid(sid, path)
            res.blogFileInfos.addAll(blogInfo.blogFileInfos)
        }
        return res
    }

    fun getBlogNamespaces(): BlogNamespaces {
        val blogNamespaceSet = HashSet<BlogNamespace>()
        blogTreeDataMap.forEach() { item ->
            val blogTreeData = item.value

            for (blogTreeNode in blogTreeData) {
                if (!blogTreeNode.isFolder) {
                    continue
                }
                blogNamespaceSet.add(BlogNamespace(blogTreeNode.title))
            }
        }
        return BlogNamespaces(blogNamespaceSet)
    }
}