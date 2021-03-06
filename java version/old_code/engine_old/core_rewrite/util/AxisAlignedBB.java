package team.hdt.blockadia.engine.core_rewrite.util;

/**
 * <em><b>Copyright (c) 2018 Ocelot5836.</b></em>
 * 
 * <br>
 * </br>
 * 
 * Used to handle collisions in the 2D world.
 * 
 * @author Ocelot5836
 */
public class AxisAlignedBB {

	public static final AxisAlignedBB EMPTY_AABB = new AxisAlignedBB();

	private double x;
	private double y;
	private double width;
	private double height;

	/**
	 * Creates a new, empty AABB.
	 */
	public AxisAlignedBB() {
		this(0, 0, 0, 0);
	}

	/**
	 * Creates a new AABB with the specified parameters.
	 */
	public AxisAlignedBB(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	/**
	 * Checks if this intersects with another AABB.
	 * 
	 * @param other
	 *            The other AAB to check intersections with
	 * @return Whether or not the two boxes intersect
	 */
	public boolean intersects(AxisAlignedBB other) {
		return !(this.getXMax() < other.getX() || other.getXMax() < this.getX() || this.getYMax() < other.getY() || other.getYMax() < this.getY());
	}

	/**
	 * Sets the x and y positions in the AABB
	 * 
	 * @param x
	 *            The new x
	 * @param y
	 *            The new y
	 */
	public AxisAlignedBB set(double x, double y) {
		this.x = x;
		this.y = y;
		return this;
	}

	/**
	 * Adds the x and y positions to the positions in the AABB
	 * 
	 * @param x
	 *            The amount to add to the x
	 * @param y
	 *            The amount to add to the y
	 */
	public AxisAlignedBB add(double x, double y) {
		this.x += x;
		this.y += y;
		return this;
	}

	/**
	 * Subtracts the x and y positions to the positions in the AABB
	 * 
	 * @param x
	 *            The amount to take from the x
	 * @param y
	 *            The amount to take from the y
	 */
	public AxisAlignedBB sub(double x, double y) {
		this.x -= x;
		this.y -= y;
		return this;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getXMax() {
		return x + width;
	}

	public double getYMax() {
		return y + height;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public AxisAlignedBB copy() {
		return this.copy(new AxisAlignedBB());
	}

	public AxisAlignedBB copy(AxisAlignedBB aabb) {
		aabb.x = x;
		aabb.y = y;
		aabb.width = width;
		aabb.height = height;
		return aabb;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return this.copy();
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[" + this.x + ", " + this.y + ", " + this.x + this.width + ", " + this.y + this.height + "]";
	}

	public static boolean intersects(double x, double y, double width, double height, double otherX, double otherY, double otherWidth, double otherHeight) {
		return !(x + width < otherX || otherX + otherWidth < x || y + height < y || otherY + otherHeight < x);
	}
}