package com.dc2f.lsp

import java.io.File
import java.net.URLClassLoader

class Dc2fCompletion(val jarFile: File, val mainClassName: String) {
    private var classLoader: URLClassLoader

    init {
        classLoader = URLClassLoader(arrayOf(jarFile.toURI().toURL()), this.javaClass.classLoader)
    }
}