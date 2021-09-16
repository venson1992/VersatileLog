package com.venson.versatile.lint;

import com.android.tools.lint.client.api.IssueRegistry;
import com.android.tools.lint.detector.api.ApiKt;
import com.android.tools.lint.detector.api.Issue;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import kotlin.Suppress;

@Suppress(names = "UnstableApiUsage")
public final class CustomIssueRegistry extends IssueRegistry {
    @NotNull
    @Override
    public List<Issue> getIssues() {
        return Collections.singletonList(LogDetector.LOG_ISSUE);
    }

    @Override
    public int getApi() {
        return ApiKt.CURRENT_API;
    }
}