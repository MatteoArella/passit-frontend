package com.github.passit.core.domain

abstract class Mapper<in E, out T> {
    abstract fun map(from: E): T
}