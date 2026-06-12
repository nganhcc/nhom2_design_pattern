package com.group.videosharing.patterns.structural.decorator;

import com.group.videosharing.patterns.behavioral.strategy.SearchStrategy;

/** Decorator — Pattern 2. Abstract base wrap một SearchStrategy khác. */
public abstract class SearchResultDecorator implements SearchStrategy {
    protected final SearchStrategy wrapped;

    public SearchResultDecorator(SearchStrategy wrapped) {
        this.wrapped = wrapped;
    }
}
