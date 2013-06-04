/*
	Dehydration Class

	- dehydration calculation with heat index formula
	- assume weather data is provided in "(temperature[F],relative humidity[%])"

*/

import java.lang.Math;

class Dehydration
{

// private variables
	private String[] weather;
	private int activity;	// from accelerometer
	private int intensity;	// from profile, [1: easy, 2: moderate, 3: hard]
	private int time;		// from timer, in minutes
	private int weight; 	// from profile, in lbs
	private double vwl;		// volume of water lost
	private double mwl;		// mass of water lost
	private double bwl;		// percent body weight lost

// public variables
	public int temperature;
	public int relativeHumidity;
	public double heatIndex;
	public int heatCategory;
	public double water;
	public int dehydrationLevel;	// min: 1, max: 5


// private methods
	private double calcHeatIndex (int t, int r)
	{
		double hi = 16.923 + (1.85212 * Math.pow(10,-1) * t) + (5.37941 * r) - (1.00254 * Math.pow(10,-1) * t * r)
				+ (9.41695 * Math.pow(10,-3) * Math.pow(t,2)) + (7.28898 * Math.pow (10,-3) * Math.pow(r,2)) + (3.45372 * Math.pow(10,-4) * Math.pow(t,2) * r)
				- (8.14971 * Math.pow(10,-4) * t * Math.pow(r,2)) + (1.02102 * Math.pow(10,-5) * Math.pow(t,2) * Math.pow(r,2)) - (3.8646 * Math.pow(10,-5) * Math.pow(t,3))
				+ (2.91583 * Math.pow(10,-5) * Math.pow(r,3)) + (1.42721 * Math.pow(10,-6) * Math.pow(t,3) * r) + (1.97483 * Math.pow(10,-7) * t * Math.pow(r,3))
				- (2.18429 * Math.pow (10,-8) * Math.pow(t,3) * Math.pow(r,2)) + (8.43296 * Math.pow(10,-10) * Math.pow(t,2) * Math.pow(r,3)) - (4.81975 * Math.pow(10,-11) * Math.pow(t,3) * Math.pow (r,3));
		return hi;
	}

	private int calcHeatCategory (double hi) 
	{
		if (hi < 80)
		{
			if (intensity == 1)
				water = 0.5;
			else if (intensity == 2)
				water = 0.75;
			else
				water = 0.75;
			return 1;	// normal
		}
		else if (hi < 91)
		{
			if (intensity == 1)
				water = 0.5;
			else if (intensity == 2)
				water = 0.75;
			else
				water = 1;
			return 2;	// caution
		}
		else if (hi < 104)
		{
			if (intensity == 1)
				water = 0.75;
			else if (intensity == 2)
				water = 0.75;
			else
				water = 1;
			return 3;	// extreme caution
		}
		else if (hi < 125)
		{
			if (intensity == 1)
				water = 0.75;
			else if (intensity == 2)
				water = 0.75;
			else
				water = 1;
			return 4;	// danger
		}
		else
		{
			if (intensity == 1)
				water = 1;
			else if (intensity == 2)
				water = 1;
			else
				water = 1;
			return 5;	// extreme danger
		}
	}

	private double calcBodyWeightLoss ()
	{
		vwl = time / 60 * water;
		mwl = vwl * 946.4 * 0.001;
		return mwl / weight * 2.203;
	}

// constructor
	public Dehydration (String wd)
	{
		String delims = "[,]";
		weather = wd.split(delims);
		temperature = Integer.parseInt(weather[0]);
		relativeHumidity = Integer.parseInt(weather[1]);
		heatIndex = calcHeatIndex (temperature, relativeHumidity);
		heatCategory = calcHeatCategory (heatIndex);
		// activity = accelerometer.getActivity();
		// intensity = profile.getIntensity();
		// weight = profile.getWeight();
		// time = system.getTimer();
		
		bwl = calcBodyWeightLoss ();

		// 5 levels of dehydration
		if (bwl <= 2)
			dehydrationLevel = 1;
		else if (bwl <= 4)
			dehydrationLevel = 2;
		else if (bwl <= 6)
			dehydrationLevel = 3;
		else if (bwl <= 8)
			dehydrationLevel = 4;
		else
			dehydrationLevel = 5;
	}

/*
	// get temperature (F)
	public int getTemperature ()
	{
		return temperature;
	}

	// get relative humidity (%) 
	public int getHumidity () 
	{
		return relativeHumidity;
	}

	// get heat index value
	public int getHeatIndex ()
	{
		return heatIndex;
	}

	// get heat category (1-5)
	public int getHeatCategory ()
	{
		return heatCategory;
	}

	// get amount of water (quart) recommended to drink/hour
	public double getWaterRecommendation () {
		return water;
	}
*/


	/*public static void main (String[] args)
	{
		//System.out.println(heatIndex(80,40));
		//System.out.println(getHeatLevel(heatIndex(100,50)));
	}*/
}