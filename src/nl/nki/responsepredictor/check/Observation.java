package nl.nki.responsepredictor.check;

import java.util.Arrays;
import java.util.LinkedHashMap;

public class Observation {
    
    String id;
    String name;
    LinkedHashMap<String,Double> start;
    String[] fixed;
    LinkedHashMap<String,Double> end;
    
    public String[] getFixed() {
        return fixed;
    }

    public void setFixed(String[] fixed) {
        this.fixed = fixed;
    }

    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedHashMap<String, Double> getStart() {
        return start;
    }

    public void setStart(LinkedHashMap<String, Double> start) {
        this.start = start;
    }

    public LinkedHashMap<String, Double> getEnd() {
        return end;
    }

    public void setEnd(LinkedHashMap<String, Double> end) {
        this.end = end;
    }
    
    
    public Observation() {
		super();    	
    }

        
	public Observation(String id, String name,
			LinkedHashMap<String, Double> start, String[] fixed,
			LinkedHashMap<String, Double> end) {

		this.id = id;
		this.name = name;
		this.start = start;
		this.fixed = fixed;
		this.end = end;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + Arrays.hashCode(fixed);
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
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
		Observation other = (Observation) obj;
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		if (!Arrays.equals(fixed, other.fixed))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		return true;
	}
    
    



}


