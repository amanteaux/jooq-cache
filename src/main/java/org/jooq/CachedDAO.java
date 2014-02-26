package org.jooq;

public interface CachedDAO<R extends TableRecord<R>, P, T> extends DAOExtended<R, P, T> {

	CachedConfiguration configuration();
	
}
