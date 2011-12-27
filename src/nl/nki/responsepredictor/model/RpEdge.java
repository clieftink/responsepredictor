package nl.nki.responsepredictor.model;

import java.util.Arrays;
import java.util.HashMap;

public class RpEdge {
	String id;
	String label;
	String source;
	String target;
	int interaction; // 1: activation, -1 : inhibition
	double weight;
	HashMap<String, String> refs;
	String notes;

	public RpEdge(String source, String target, int interaction) {
		this(null, null, source, target, interaction,0, null, null);
	}

	public RpEdge(String id, String label, String source, String target,
			int interaction) {
		this(id, label, source, target, interaction,0, null, null);
	}

	/**
	 * Used especially for updates.
	 * 
	 * @param id
	 * @param interaction
	 */
	public RpEdge(String id, int interaction) {
		this(id, null, null, null, interaction, 0,null, null);
	}

	/**
	 * @param id
	 * @param label
	 * @param source
	 * @param target
	 * @param interaction
	 * @param refs
	 * @param notes
	 */
	public RpEdge(String id, String label, String source, String target,
			int interaction, double weight, HashMap<String, String> refs, String notes) {
		super();
		this.id = id;
		this.label = label;
		this.source = source;
		this.target = target;
		this.interaction = interaction;
		this.weight= weight;
		this.refs = refs;
		this.notes = notes;
	}

	/* Used to transport (partial) mutation information */
	public RpEdge() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public int getInteraction() {
		return interaction;
	}

	public void setInteraction(int interaction) {
		this.interaction = interaction;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public HashMap<String, String> getRefs() {
		return refs;
	}

	public void setRefs(HashMap<String, String> refs) {
		this.refs = refs;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + interaction;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		result = prime * result + ((refs == null) ? 0 : refs.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		long temp;
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
		RpEdge other = (RpEdge) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (interaction != other.interaction)
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
		if (refs == null) {
			if (other.refs != null)
				return false;
		} else if (!refs.equals(other.refs))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		if (Double.doubleToLongBits(weight) != Double
				.doubleToLongBits(other.weight))
			return false;
		return true;
	}



}