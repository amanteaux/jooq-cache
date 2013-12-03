package org.jooq.impl;

import java.util.Set;

import org.jooq.Clause;
import org.jooq.VisitContext;
import org.jooq.VisitListener;

public class CachedVisitListener implements VisitListener {

	private final Set<String> referencedTables;

	public CachedVisitListener(Set<String> referencedTables) {
		this.referencedTables = referencedTables;
	}

	@Override
	public void visitStart(VisitContext context) {
		if (context.clause() == Clause.TABLE_REFERENCE) {
			referencedTables.add(context.queryPart().toString());
		}
	}

	// do nothing

	@Override
	public void clauseStart(VisitContext context) {
	}

	@Override
	public void clauseEnd(VisitContext context) {
	}

	@Override
	public void visitEnd(VisitContext context) {
	}

}
