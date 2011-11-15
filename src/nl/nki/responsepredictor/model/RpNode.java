package nl.nki.responsepredictor.model;

import java.util.HashMap;

/*
 * Called RpNode to distinguish it from Node in a XML
 * 
 */

public class RpNode {
	String id;
	String canonicalName; // would be redundant info compare to label
	String label; // can be customized by the user, fe. shortening the
					// canonicalName
	int type; // value 1: player, -2: andGate:
	double weight;
	double state; // 0: inactive, 1: active
	String notes;
	HashMap<String, String> idMaps;
	
	public RpNode(String id) {
		this(id,null,null,1);
	}	

	public RpNode(String id, String canonicalName, String label, int type) {
		this(id,canonicalName,label,type,0,0,"",null);
	}

	public RpNode(String id, String canonicalName, String label, int type, double weight) {
		this(id,canonicalName,label,type,weight,0,"",null);
	}
	
	public RpNode(String id, String canonicalName, String label, int type, double weight,
			double state, String notes, HashMap<String, String> idMaps) {
		super();
		this.id = id;
		this.canonicalName = canonicalName;
		this.label = label;
		this.type = type;
		this.weight= weight;
		this.state = state;
		this.notes = notes;
		this.idMaps = idMaps;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCanonicalName() {
		return canonicalName;
	}

	public void setCanonicalName(String canonicalName) {
		this.canonicalName = canonicalName;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
		public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getState() {
		return state;
	}

	public void setState(double state) {
		this.state = state;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public HashMap<String, String> getIdMaps() {
		return idMaps;
	}

	public void setIdMaps(HashMap<String, String> idMaps) {
		this.idMaps = idMaps;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((canonicalName == null) ? 0 : canonicalName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((idMaps == null) ? 0 : idMaps.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		long temp;
		temp = Double.doubleToLongBits(state);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + type;
		temp = Double.doubleToLongBits(weight);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RpNode other = (RpNode) obj;
		if (canonicalName == null) {
			if (other.canonicalName != null)
				return false;
		} else if (!canonicalName.equals(other.canonicalName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (idMaps == null) {
			if (other.idMaps != null)
				return false;
		} else if (!idMaps.equals(other.idMaps))
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (notes == null) {
			if (other.notes != null)
				return false;
		} else if (!notes.equals(other.notes))
			return false;
		if (Double.doubleToLongBits(state) != Double
				.doubleToLongBits(other.state))
			return false;
		if (type != other.type)
			return false;
		if (Double.doubleToLongBits(weight) != Double
				.doubleToLongBits(other.weight))
			return false;
		return true;
	}
	
	

}