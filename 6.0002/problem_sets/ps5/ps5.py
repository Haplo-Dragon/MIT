# -*- coding: utf-8 -*-
# Problem Set 5: Experimental Analysis
# Name:
# Collaborators (discussion):
# Time:

import pylab
import re

# cities in our weather data
CITIES = [
    "BOSTON",
    "SEATTLE",
    "SAN DIEGO",
    "PHILADELPHIA",
    "PHOENIX",
    "LAS VEGAS",
    "CHARLOTTE",
    "DALLAS",
    "BALTIMORE",
    "SAN JUAN",
    "LOS ANGELES",
    "MIAMI",
    "NEW ORLEANS",
    "ALBUQUERQUE",
    "PORTLAND",
    "SAN FRANCISCO",
    "TAMPA",
    "NEW YORK",
    "DETROIT",
    "ST LOUIS",
    "CHICAGO",
]

TRAINING_INTERVAL = range(1961, 2010)
TESTING_INTERVAL = range(2010, 2016)

PLOT_TITLE = "Mean annual temps in NYC 1964-2010\nR-squared: {} Degree: {}\n"
PLOT_SE_SLOPE = "Standard error of fitted curve slope / Data slope: {}"
PLOT_X_LABEL = "Years"
PLOT_Y_LABEL = "Degrees Celsius"

"""
Begin helper code
"""


class Climate(object):
    """
    The collection of temperature records loaded from given csv file
    """

    def __init__(self, filename):
        """
        Initialize a Climate instance, which stores the temperature records
        loaded from a given csv file specified by filename.

        Args:
            filename: name of the csv file (str)
        """
        self.rawdata = {}

        f = open(filename, "r")
        header = f.readline().strip().split(",")
        for line in f:
            items = line.strip().split(",")

            date = re.match("(\d\d\d\d)(\d\d)(\d\d)", items[header.index("DATE")])
            year = int(date.group(1))
            month = int(date.group(2))
            day = int(date.group(3))

            city = items[header.index("CITY")]
            temperature = float(items[header.index("TEMP")])
            if city not in self.rawdata:
                self.rawdata[city] = {}
            if year not in self.rawdata[city]:
                self.rawdata[city][year] = {}
            if month not in self.rawdata[city][year]:
                self.rawdata[city][year][month] = {}
            self.rawdata[city][year][month][day] = temperature

        f.close()

    def get_yearly_temp(self, city, year):
        """
        Get the daily temperatures for the given year and city.

        Args:
            city: city name (str)
            year: the year to get the data for (int)

        Returns:
            a 1-d pylab array of daily temperatures for the specified year and
            city
        """
        temperatures = []
        assert city in self.rawdata, "provided city is not available"
        assert year in self.rawdata[city], "provided year is not available"
        for month in range(1, 13):
            for day in range(1, 32):
                if day in self.rawdata[city][year][month]:
                    temperatures.append(self.rawdata[city][year][month][day])
        return pylab.array(temperatures)

    def get_daily_temp(self, city, month, day, year):
        """
        Get the daily temperature for the given city and time (year + date).

        Args:
            city: city name (str)
            month: the month to get the data for (int, where January = 1,
                December = 12)
            day: the day to get the data for (int, where 1st day of month = 1)
            year: the year to get the data for (int)

        Returns:
            a float of the daily temperature for the specified time (year +
            date) and city
        """
        assert city in self.rawdata, "provided city is not available"
        assert year in self.rawdata[city], "provided year is not available"
        assert month in self.rawdata[city][year], "provided month is not available"
        assert day in self.rawdata[city][year][month], "provided day is not available"
        return self.rawdata[city][year][month][day]


def se_over_slope(x, y, estimated, model):
    """
    For a linear regression model, calculate the ratio of the standard error of
    this fitted curve's slope to the slope. The larger the absolute value of
    this ratio is, the more likely we have the upward/downward trend in this
    fitted curve by chance.

    Args:
        x: an 1-d pylab array with length N, representing the x-coordinates of
            the N sample points
        y: an 1-d pylab array with length N, representing the y-coordinates of
            the N sample points
        estimated: an 1-d pylab array of values estimated by a linear
            regression model
        model: a pylab array storing the coefficients of a linear regression
            model

    Returns:
        a float for the ratio of standard error of slope to slope
    """
    assert len(y) == len(estimated)
    assert len(x) == len(estimated)
    EE = ((estimated - y) ** 2).sum()
    var_x = ((x - x.mean()) ** 2).sum()
    SE = pylab.sqrt(EE / (len(x) - 2) / var_x)
    return SE / model[0]


"""
End helper code
"""


def generate_models(x, y, degs):
    """
    Generate regression models by fitting a polynomial for each degree in degs
    to points (x, y).

    Args:
        x: an 1-d pylab array with length N, representing the x-coordinates of
            the N sample points
        y: an 1-d pylab array with length N, representing the y-coordinates of
            the N sample points
        degs: a list of degrees of the fitting polynomial

    Returns:
        a list of pylab arrays, where each array is a 1-d array of coefficients
        that minimizes the squared error of the fitting polynomial
    """
    models = []
    for degree in degs:
        model = pylab.polyfit(x, y, degree)
        models.append(model)
    return models


def r_squared(y, estimated):
    """
    Calculate the R-squared error term.

    Args:
        y: 1-d pylab array with length N, representing the y-coordinates of the
            N sample points
        estimated: an 1-d pylab array of values estimated by the regression
            model

    Returns:
        a float for the R-squared error term
    """
    estimated_error = ((y - estimated)**2).sum()
    mean_samples = (y.sum()) / len(y)
    measured_variability = ((y - mean_samples)**2).sum()

    return 1 - estimated_error / measured_variability


def evaluate_models_on_training(x, y, models):
    """
    For each regression model, compute the R-squared value for this model with the
    standard error over slope of a linear regression line (only if the model is
    linear), and plot the data along with the best fit curve.

    For the plots, you should plot data points (x,y) as blue dots and your best
    fit curve (aka model) as a red solid line. You should also label the axes
    of this figure appropriately and have a title reporting the following
    information:
        degree of your regression model,
        R-square of your model evaluated on the given data points,
        and SE/slope (if degree of this model is 1 -- see se_over_slope).

    Args:
        x: an 1-d pylab array with length N, representing the x-coordinates of
            the N sample points
        y: an 1-d pylab array with length N, representing the y-coordinates of
            the N sample points
        models: a list containing the regression models you want to apply to
            your data. Each model is a pylab array storing the coefficients of
            a polynomial.

    Returns:
        None
    """
    for model in models:
        # Find degree of model. If model is degree 1 (a line), find difference between
        # model's slope and data's slope
        model_degree = len(model) - 1
        model_estimated_values = pylab.polyval(model, x)

        if model_degree == 1:
            model_se_slope = se_over_slope(x, y, model_estimated_values, model)
        else:
            model_se_slope = None

        # Find R squared of model
        model_r_squared = r_squared(y, model_estimated_values)

        # Plot fitted curve from model as a solid red line
        pylab.plot(
            x,
            model_estimated_values,
            color='red',
            linestyle='solid',
            label="Fit of degree {}".format(model_degree) +
            " R-squared: {}".format(round(model_r_squared, 5)))

    # Plot measured data as blue dots
    pylab.plot(x, y, color='blue', marker='o', linestyle='None', label="Measured data")

    # Label axes
    pylab.xlabel(PLOT_X_LABEL)
    pylab.ylabel(PLOT_Y_LABEL)

    # Title plot
    title = PLOT_TITLE.format(round(model_r_squared, 5), model_degree)
    if model_se_slope:
        title += PLOT_SE_SLOPE.format(round(model_se_slope, 5))
    pylab.title(title)

    # Show plot and legend
    pylab.legend()
    pylab.show()


def gen_cities_avg(climate, multi_cities, years):
    """
    Compute the average annual temperature over multiple cities.

    Args:
        climate: instance of Climate
        multi_cities: the names of cities we want to average over (list of str)
        years: the range of years of the yearly averaged temperature (list of
            int)

    Returns:
        a pylab 1-d array of floats with length = len(years). Each element in
        this array corresponds to the average annual temperature over the given
        cities for a given year.
    """
    # TODO
    pass


def moving_average(y, window_length):
    """
    Compute the moving average of y with specified window length.

    Args:
        y: an 1-d pylab array with length N, representing the y-coordinates of
            the N sample points
        window_length: an integer indicating the window length for computing
            moving average

    Returns:
        an 1-d pylab array with the same length as y storing moving average of
        y-coordinates of the N sample points
    """
    # TODO
    pass


def rmse(y, estimated):
    """
    Calculate the root mean square error term.

    Args:
        y: an 1-d pylab array with length N, representing the y-coordinates of
            the N sample points
        estimated: an 1-d pylab array of values estimated by the regression
            model

    Returns:
        a float for the root mean square error term
    """
    # TODO
    pass


def gen_std_devs(climate, multi_cities, years):
    """
    For each year in years, compute the standard deviation over the averaged yearly
    temperatures for each city in multi_cities.

    Args:
        climate: instance of Climate
        multi_cities: the names of cities we want to use in our std dev calculation
            (list of str)
        years: the range of years to calculate standard deviation for (list of int)

    Returns:
        a pylab 1-d array of floats with length = len(years). Each element in
        this array corresponds to the standard deviation of the average annual
        city temperatures for the given cities in a given year.
    """
    # TODO
    pass


def evaluate_models_on_testing(x, y, models):
    """
    For each regression model, compute the RMSE for this model and plot the
    test data along with the model’s estimation.

    For the plots, you should plot data points (x,y) as blue dots and your best
    fit curve (aka model) as a red solid line. You should also label the axes
    of this figure appropriately and have a title reporting the following
    information:
        degree of your regression model,
        RMSE of your model evaluated on the given data points.

    Args:
        x: an 1-d pylab array with length N, representing the x-coordinates of
            the N sample points
        y: an 1-d pylab array with length N, representing the y-coordinates of
            the N sample points
        models: a list containing the regression models you want to apply to
            your data. Each model is a pylab array storing the coefficients of
            a polynomial.

    Returns:
        None
    """
    # TODO
    pass


if __name__ == "__main__":
    # Part A.4
    c = Climate('data.csv')
    # New York
    city = CITIES[-4]

    # NY_Jan10_data = []
    # years = []
    # for year in TRAINING_INTERVAL:
    #     NY_Jan10_data.append(c.get_daily_temp(city, 1, 10, year))
    #     years.append(year)

    # NY_Jan10_data = pylab.array(NY_Jan10_data)
    # years = pylab.array(years)
    # # Fit a degree 1 model.
    # models = generate_models(years, NY_Jan10_data, [1])
    # evaluate_models_on_training(years, NY_Jan10_data, models)

    # Now we'll plot the mean average temperature for NYC
    NY_annual_avg_data = []
    for year in TRAINING_INTERVAL:
        # Get all daily temperatures for the current year
        current_year_temps = c.get_yearly_temp(city, year)
        # Add the mean of those daily temperatures to the data
        NY_annual_avg_data.append(sum(current_year_temps) / len(current_year_temps))

    NY_annual_avg_data = pylab.array(NY_annual_avg_data)
    years = pylab.array(TRAINING_INTERVAL)

    # Fit a degree 1 model.
    models = generate_models(years, NY_annual_avg_data, [1])
    evaluate_models_on_training(years, NY_annual_avg_data, models)

    # Part B
    # TODO: replace this line with your code

    # Part C
    # TODO: replace this line with your code

    # Part D.2
    # TODO: replace this line with your code

    # Part E
    # TODO: replace this line with your code
