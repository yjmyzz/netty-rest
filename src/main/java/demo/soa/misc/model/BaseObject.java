package demo.soa.misc.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public abstract class BaseObject implements Serializable {
	
		/**
	 * 
	 */
	private static final long serialVersionUID = -1057376251431539014L;
		/* (non-Javadoc)
	    * @see java.lang.Object#toString()
	    *
	    */
	    @Override
		public String toString() {
	       return ReflectionToStringBuilder.reflectionToString(this,
	              ToStringStyle.MULTI_LINE_STYLE);
	    }
	    /* (non-Javadoc)
	    * @see java.lang.Object#equals(java.lang.Object)
	    */
	    @Override
		public boolean equals(Object obj) {
	       return EqualsBuilder.reflectionEquals(this, obj);
	    }
	    /* (non-Javadoc)
	    * @see java.lang.Object#hashCode()
	    */
	    @Override
		public int hashCode() {
	       return HashCodeBuilder.reflectionHashCode(this);
	    }
	    
	    

}
