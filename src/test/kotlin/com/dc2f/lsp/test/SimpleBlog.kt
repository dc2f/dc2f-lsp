package com.dc2f.lsp.test

import com.dc2f.*
import com.dc2f.richtext.markdown.Markdown
import com.fasterxml.jackson.annotation.JacksonInject

interface BlogSeo : ContentDef {
    val title: String
    val description: String?
    val keywords: String?
}

interface BlogAuthor : ContentDef {
    val firstName: String
    val lastName: String
}

@Nestable("article")
interface BlogArticle : ContentDef {
    val headline: String
    val category: String
    val seo: BlogSeo
    val author: BlogAuthor

}

@Nestable("page")
interface ContentPage : ContentDef {
    @set:JacksonInject("body")
    var body: Markdown
}

interface ContentFolder : ContentBranchDef<ContentPage>

@Nestable("blog")
interface BlogFolder : ContentBranchDef<BlogArticle> {
    val seo: BlogSeo
}

sealed class SimpleBlogFolder : ContentDef {
    abstract class Content : SimpleBlogFolder(), ContentFolder
    abstract class Blog : SimpleBlogFolder(), BlogFolder
}

interface SimpleBlog : Website<SimpleBlogFolder>
