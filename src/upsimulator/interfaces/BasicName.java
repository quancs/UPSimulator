package upsimulator.interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BasicName implements Name {
	private ArrayList<Dimension> dimensions;
	private boolean fixed = true;
	private String name;

	public BasicName() {
		super();
		this.fixed = true;
	}

	public BasicName(String name) {
		super();
		this.fixed = true;
		this.name = name;
	}

	public BasicName(String name, String... dimensions) {
		super();
		this.fixed = true;
		this.name = name;
		for (String dString : dimensions)
			addDimension(dString);
	}

	public BasicName(String name, ArrayList<Dimension> dimensions) {
		super();
		this.name = name;
		this.dimensions = dimensions;
		this.fixed = true;
		for (Dimension dimension : dimensions)
			if (dimension.isFixed() == false) {
				fixed = false;
				break;
			}
	}

	public BasicName(String name, ArrayList<Dimension> dimensions, boolean fixed) {
		super();
		this.name = name;
		this.dimensions = dimensions;
		this.fixed = fixed;
	}

	public BasicName(BasicName dimensional) {
		super();
		if (dimensional.isFixed() == false && dimensional.getDimensionSize() > 0) {
			this.dimensions = new ArrayList<>(dimensional.getDimensionSize());
			for (Dimension dimension : dimensional.dimensions)
				this.dimensions.add(new Dimension(dimension));
		} else
			this.dimensions = dimensional.dimensions;
		this.fixed = dimensional.fixed;
		this.name = dimensional.name;
		this.nameDim = dimensional.nameDim;
	}

	@Override
	public void addDimension(Dimension dimension) {
		if (dimensions == null)
			dimensions = new ArrayList<>();
		dimensions.add(dimension);
		if (dimension.isFixed() == false)
			fixed = false;
	}

	@Override
	public void addDimension(Long dimension) {
		if (dimensions == null)
			dimensions = new ArrayList<>();
		dimensions.add(new Dimension(dimension.toString(), new Long(dimension.toString())));
		if (dimensions.size() == 1)
			fixed = true;
	}

	@Override
	public void addDimension(String dimension) {
		if (dimensions == null)
			dimensions = new ArrayList<>();
		dimensions.add(new Dimension(dimension));
		fixed = false;
	}

	@Override
	public ArrayList<Dimension> getDimensions() {
		return dimensions;
	}

	@Override
	public void fix(Map<String, Object> mappedValues) {
		if (dimensions == null) {
			fixed = true;
			return;
		}
		for (Dimension dimension : dimensions)
			dimension.fix(mappedValues);
		fixed = true;
	}

	@Override
	public boolean isFixed() {
		return fixed;
	}

	@Override
	public int getDimensionSize() {
		if (dimensions == null)
			return 0;
		else
			return dimensions.size();
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	private String nameDim;
	private boolean nameDimStatus;

	@Override
	public String getNameDim() {
		if (nameDim == null) {
			nameDim = name;
			if (getDimensionSize() > 0) {
				for (Dimension dimension : dimensions)
					nameDim += "[" + dimension + "]";
			}
			nameDimStatus = isFixed();
		} else {
			if (nameDimStatus != isFixed()) {
				nameDim = name;
				if (getDimensionSize() > 0) {
					for (Dimension dimension : dimensions)
						nameDim += "[" + dimension + "]";
				}
				nameDimStatus = isFixed();
			}
		}
		return nameDim;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Dimension get(int i) {
		if (i >= getDimensionSize()) {
			throw new ArrayIndexOutOfBoundsException(i);
		} else {
			return dimensions.get(i);
		}
	}

	@Override
	public BasicName deepClone() {
		if (isFixed())
			return this;
		else
			return new BasicName(this);
	}

	private HashMap<Integer, Integer> getDimensionInfoMap(List<Dimension> dList) {
		HashMap<Integer, Integer> dMap = new HashMap<>();
		for (int i = 0; i < getDimensionSize(); i++) {
			Dimension d = get(i);
			if (d.isFixed())
				continue;
			for (int j = 0; j < dList.size(); j++) {
				if (d.equals(dList.get(j))) {
					dMap.put(i, j);
					break;
				}
			}
		}
		return dMap;
	}

	private LinkedList<Long[]> doPredict(HashMap<Integer, Integer> dMap, Iterator<?> iter, List<Dimension> dList) {
		LinkedList<Long[]> pValues = new LinkedList<>();

		while (iter.hasNext()) {
			Name obj = (Name) iter.next();
			if (obj.getName().equals(getName()) && obj.getDimensionSize() == getDimensionSize()) {
				Long pValue[] = new Long[dList.size()];

				for (int i = 0; i < obj.getDimensionSize(); i++) {
					if (dMap.containsKey(i))
						pValue[dMap.get(i)] = obj.get(i).getLongValue();
				}
				pValues.add(pValue);
			}
		}
		return pValues;
	}

	@Override
	public List<Long[]> predictPossibleValue(Membrane membrane, List<Dimension> dList) {
		return predictPossibleValueOfObjects(membrane, dList);
	}

	public List<Long[]> predictPossibleValueOfObjects(Membrane membrane, List<Dimension> dList) {
		if (isFixed())
			return null;
		else {
			// Initial dimension information
			HashMap<Integer, Integer> dMap = getDimensionInfoMap(dList);
			if (dMap.size() == 0)
				return null;

			// Generate possible value list
			Iterator<?> iter = membrane.getObjects().keySet().iterator();
			return doPredict(dMap, iter, dList);
		}
	}

	public List<Long[]> predictPossibleValueOfSubmembrane(Membrane membrane, List<Dimension> dList) {
		if (isFixed())
			return null;
		else {
			// Initial dimension information
			HashMap<Integer, Integer> dMap = getDimensionInfoMap(dList);
			if (dMap.size() == 0)
				return null;

			// Generate possible value list
			Iterator<?> iter = membrane.getChildren().iterator();
			return doPredict(dMap, iter, dList);
		}
	}

	public List<Long[]> predictPossibleValueOfNeighbor(Membrane membrane, List<Dimension> dList) {
		if (isFixed())
			return null;
		else {
			// Initial dimension information
			HashMap<Integer, Integer> dMap = getDimensionInfoMap(dList);
			if (dMap.size() == 0)
				return null;

			// Generate possible value list
			Iterator<?> iter = membrane.getNeighbors().iterator();
			return doPredict(dMap, iter, dList);
		}
	}
}
