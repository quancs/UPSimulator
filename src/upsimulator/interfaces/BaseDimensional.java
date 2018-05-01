package upsimulator.interfaces;

import java.util.ArrayList;
import java.util.Map;

public abstract class BaseDimensional implements Name, Dimensional {
	private ArrayList<Dimension> dimensions;
	private boolean fixed = true;
	private String name;

	public BaseDimensional() {
		super();
		this.fixed = true;
	}

	public BaseDimensional(ArrayList<Dimension> dimensions) {
		super();
		this.dimensions = dimensions;
		this.fixed = true;
		for (Dimension dimension : dimensions)
			if (dimension.isFixed() == false) {
				fixed = false;
				break;
			}
	}

	public BaseDimensional(ArrayList<Dimension> dimensions, boolean fixed) {
		super();
		this.dimensions = dimensions;
		this.fixed = fixed;
	}

	public BaseDimensional(BaseDimensional dimensional) {
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
}
