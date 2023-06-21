package edu.usst.jayce.server.mapper;

import edu.usst.jayce.server.model.Propertie;

public interface PropertiesMapper {
	
	int insert(final Propertie p);
	
	int deleteByKey(final String propertiesKey);
	
	Propertie selectByKey(final String propertiesKey);
	
	int update(final Propertie p);
	
}
