package org.treez.javafxd3.d3.svg;

import org.treez.javafxd3.d3.functions.DataFunction;

import org.treez.javafxd3.d3.core.JsEngine;
import org.treez.javafxd3.d3.core.JsObject;

/**
 * Generate a piecewise linear curve, as in a line chart.
 * <p>
 * Data must be an array-like structure. the type of the array elements depends
 * on the x and y functions. the default x and y functions assumes that each
 * input element is a two-element array of numbers.
 * <p>
 */
public class Line extends PathDataGenerator {

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 * 
	 * @param engine
	 * @param wrappedJsObject
	 */
	public Line(JsEngine engine, JsObject wrappedJsObject) {
		super(engine, wrappedJsObject);
	}

	//#end region

	//#region METHODS

	/**
	 * Returns the current interpolation mode.
	 * 
	 * @return the current interpolation mode.
	 */
	public InterpolationMode interpolate() {

		String result = callForString("interpolate");
		InterpolationMode interpolationMode = InterpolationMode.fromValue(result);
		return interpolationMode;

	}

	/**
	 * Set the current interpolation mode.
	 * 
	 * @param interpolationMode
	 *            the interpolation mode
	 * @return the current line
	 */
	public Line interpolate(final InterpolationMode interpolationMode) {
		String mode = interpolationMode.getValue();
		JsObject result = call("interpolate", mode);
		return new Line(engine, result);
	}

	/**
	 * Returns the current tension
	 * 
	 * @return the current tension
	 */
	public double tension() {
		Double result = callForDouble("tension");
		return result;
	}

	/**
	 * Sets the Cardinal spline interpolation tension to the specified number in
	 * the range [0, 1].
	 * <p>
	 * The tension only affects the Cardinal interpolation modes:
	 * {@link InterpolationMode#CARDINAL},
	 * {@link InterpolationMode#CARDINAL_OPEN} and
	 * {@link InterpolationMode#CARDINAL_CLOSED}.
	 * <p>
	 * The default tension is 0.7.
	 * <p>
	 * In some sense, this can be interpreted as the length of the tangent; 1
	 * will yield all zero tangents, and 0 yields a Catmull-Rom spline.
	 * 
	 * @see <a href="http://bl.ocks.org/1016220">live version</a>
	 * @param tension
	 *            the tension in the range [0, 1].
	 * @return the current line
	 */
	public Line tension(double tension) {
		JsObject result = call("tension", tension);
		return new Line(engine, result);
	}

	/**
	 * Set the x coordinates of points generated by this generator.
	 * 
	 * @param d
	 * @return
	 */
	public Line x(double d) {
		JsObject result = call("x", d);
		return new Line(engine, result);
	}

	/**
	 * Set the function used to compute x coordinates of points generated by
	 * this line generator. The function is invoked for each element in the data
	 * array passed to the line generator.
	 * <p>
	 * The default accessor assumes that each input element is a two-element
	 * array of numbers.
	 * 
	 * @param callback
	 * 
	 * @return
	 */
	public Line x(final DataFunction<?> callback) {
		JsObject result = applyDataFunction("x", callback);
		return new Line(engine, result);
	}

	// should create a JSO impl of DataFunction calling himself
	// return this.x();
	// }

	/**
	 * Set the y coordinates of points generated by this generator.
	 * 
	 * @param d
	 * @return
	 */
	public Line y(double d) {
		JsObject result = call("y", d);
		return new Line(engine, result);
	}

	/**
	 * See {@link #x(DataFunction)}.
	 * <p>
	 * Note that, like most other graphics libraries, SVG uses the top-left
	 * corner as the origin and thus higher values of y are lower on the screen.
	 * For visualization we often want the origin in the bottom-left corner
	 * instead; one easy way to accomplish this is to invert the range of the
	 * y-scale by using range([h, 0]) instead of range([0, h]).
	 * 
	 * @param callback
	 * @return
	 */
	public Line y(final DataFunction<?> callback) {
		JsObject result = applyDataFunction("y", callback);
		return new Line(engine, result);
	}

	private JsObject applyDataFunction(String methodName, final DataFunction<?> callback) {

		assertObjectIsNotAnonymous(callback);

		JsObject d3JsObject = getD3();
		String callbackName = createNewTemporaryInstanceName();

		d3JsObject.setMember(callbackName, callback);

		String command = "this." + methodName + "(function(d,i) {" //
				+ "return d3." + callbackName + ".apply(this, d, i);" //
				+ "})";
		JsObject result = evalForJsObject(command);
		
		
		if(result==null){
			return null;
		}
				
		return result;
	}

	/**
	 * Sets the function used to controls where the line is defined.
	 * <p>
	 * The defined accessor can be used to define where the line is defined and
	 * undefined, which is typically useful in conjunction with missing data;
	 * the generated path data will automatically be broken into multiple
	 * distinct subpaths, skipping undefined data.
	 * <p>
	 * 
	 * @param callback
	 *            the function to be called for each datum and returning if the
	 *            point is defined
	 * @return the current line
	 */
	public Line defined(final DataFunction<Boolean> callback) {

		JsObject d3JsObject = getD3();
		String callbackName = createNewTemporaryInstanceName();
		d3JsObject.setMember(callbackName, callback);

		String command = "this.defined(function(d) {" //
				+ "  var result = d3." + callbackName + ".apply(null, d, 0);" //
				+ "  if (result == null) { " //
				+ "       return false; " //
				+ "  } else {" //				
				+ "       return result;"//
				+ "  }"//
				+ "});";
		JsObject result = evalForJsObject(command);
		
		if(result==null){
			return null;
		}
		return new Line(engine, result);
	}
	
	

	//#end region

	}
