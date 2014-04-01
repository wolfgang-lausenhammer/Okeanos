package okeanos.math.regression.periodic;

import okeanos.math.regression.TrendLine;

public interface PeriodicTrendLine extends TrendLine {
	double getXBoundaryMin();

	double getXBoundaryMax();
}
