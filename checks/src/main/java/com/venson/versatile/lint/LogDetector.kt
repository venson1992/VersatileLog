package com.venson.versatile.lint

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import java.util.*

/**
 * 日志lint规则
 */
@Suppress("UnstableApiUsage")
class LogDetector : Detector(), Detector.UastScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>>? {
        return Collections.singletonList(UCallExpression::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler {
        return object : UElementHandler() {
            override fun visitCallExpression(node: UCallExpression) {
                if (context.evaluator.isMemberInClass(node.resolve(), "android.util.Log")) {
                    context.report(
                        LOG_ISSUE,
                        context.getLocation(node),
                        LOG_ISSUE.getExplanation(TextFormat.TEXT)
                    )
                }
            }
        }
    }

    companion object {

        @JvmField
        val LOG_ISSUE: Issue = Issue.create(
            "Log_Issue",
            "Do not use Log",
            "Please Use `VLog` instead of `android.util.Log`",
            Category.CORRECTNESS,
            6,
            Severity.ERROR,
            Implementation(LogDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}