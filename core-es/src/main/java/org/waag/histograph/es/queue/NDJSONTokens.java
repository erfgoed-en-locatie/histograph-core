package org.waag.histograph.es.queue;

public final class NDJSONTokens {
	
	public final class General {
		public static final String DATA = "data";
		public static final String LAYER = "layer";
		public static final String TYPE = "type";
		public static final String ACTION = "action";
	    public static final String HGID = "hgID";
	}
	
	public final class Types {
		public static final String PIT = "pit";
	}
	
	public final class Actions {
		public static final String ADD = "add";
		public static final String UPDATE = "update";
		public static final String DELETE = "delete";
	}
	
	public final class PITTokens {
	    public static final String ID = "id";
	    public static final String TYPE = "type";
	    public static final String NAME = "name";
	    public static final String GEOMETRY = "geometry";
	}
}