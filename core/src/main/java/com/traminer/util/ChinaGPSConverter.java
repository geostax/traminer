package com.traminer.util;

import com.graphhopper.util.GPXEntry;
import de.fhpotsdam.unfolding.geo.Location;

import java.util.Date;

/**
 * Converter to shift the GPS points to align with Google map in China.
 * @see <a href="https://en.wikipedia.org/wiki/Restrictions_on_geographic_data_in_China"/>
 */
public class ChinaGPSConverter {
    
    double casm_rr = 0;
    double casm_t1 = 0;
    double casm_t2 = 0;
    double casm_x1 = 0;
    double casm_y1 = 0;
    double casm_x2 = 0;
    double casm_y2 = 0;
    double casm_f = 0;

    private static ChinaGPSConverter converter = new ChinaGPSConverter();

    private ChinaGPSConverter(){
       
    }



    protected double yj_sin2(double x)
    {
        double tt;
        double ss;
        double ff;
        double s2;
        int cc;
        ff = 0;
        if (x < 0)
        {
            x = -x;
            ff = 1;
        }

        cc = (int)(x / 6.28318530717959);

        tt = x - cc * 6.28318530717959;
        if (tt > 3.1415926535897932)
        {
            tt = tt - 3.1415926535897932;
            if (ff == 1)
            {
                ff = 0;
            }
            else if (ff == 0)
            {
                ff = 1;
            }
        }
        x = tt;
        ss = x;
        s2 = x;
        tt = tt * tt;
        s2 = s2 * tt;
        ss = ss - s2 * 0.166666666666667;
        s2 = s2 * tt;
        ss = ss + s2 * 8.33333333333333E-03;
        s2 = s2 * tt;
        ss = ss - s2 * 1.98412698412698E-04;
        s2 = s2 * tt;
        ss = ss + s2 * 2.75573192239859E-06;
        s2 = s2 * tt;
        ss = ss - s2 * 2.50521083854417E-08;
        if (ff == 1)
        {
            ss = -ss;
        }
        return ss;
    }

    protected double Transform_yj5(double x, double y)
    {
        double tt;
        tt = 300 + 1 * x + 2 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.sqrt(x * x));
        tt = tt + (20 * this.yj_sin2(18.849555921538764 * x) + 20 * this.yj_sin2(6.283185307179588 * x)) * 0.6667;
        tt = tt + (20 * this.yj_sin2(3.141592653589794 * x) + 40 * this.yj_sin2(1.047197551196598 * x)) * 0.6667;
        tt = tt + (150 * this.yj_sin2(0.2617993877991495 * x) + 300 * this.yj_sin2(0.1047197551196598 * x)) * 0.6667;
        return tt;
    }

    protected double Transform_yjy5(double x,double y)
    {
        double tt;
        tt = -100 + 2 * x + 3 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.sqrt(x * x));
        tt = tt + (20 * this.yj_sin2(18.849555921538764 * x) + 20 * this.yj_sin2(6.283185307179588 * x)) * 0.6667;
        tt = tt + (20 * this.yj_sin2(3.141592653589794 * y) + 40 * this.yj_sin2(1.047197551196598 * y)) * 0.6667;
        tt = tt + (160 * this.yj_sin2(0.2617993877991495 * y) + 320 * this.yj_sin2(0.1047197551196598 * y)) * 0.6667;
        return tt;
    }

    protected double Transform_jy5(double x, double xx)
    {
        double n;
        double a;
        double e;
        a = 6378245;
        e = 0.00669342;
        n = Math.sqrt(1 - e * this.yj_sin2(x * 0.0174532925199433) * this.yj_sin2(x * 0.0174532925199433));
        n = (xx * 180) / (a / n * Math.cos(x * 0.0174532925199433) * 3.1415926);
        return n;
    }

    protected double Transform_jyj5(double x,double yy)
    {
        double m;
        double a;
        double e;
        double mm;
        a = 6378245;
        e = 0.00669342;
        mm = 1 - e * this.yj_sin2(x * 0.0174532925199433) * this.yj_sin2(x * 0.0174532925199433);
        m = (a * (1 - e)) / (mm * Math.sqrt(mm));
        return (yy * 180) / (m * 3.1415926);
    }


    protected int r_yj()
    {
        int casm_a = 314159269;
        int casm_c = 453806245;
        return 0;
    }

    protected double random_yj()
    {
        double t;
        double casm_a = 314159269;
        double casm_c = 453806245;
        this.casm_rr = casm_a * this.casm_rr + casm_c;
        t = (int)(this.casm_rr / 2);
        this.casm_rr = this.casm_rr - t * 2;
        this.casm_rr = this.casm_rr / 2;
        return (this.casm_rr);
    }

    protected void IniCasm(double w_time, double w_lng, double w_lat)
    {
        double tt;
        this.casm_t1 = w_time;
        this.casm_t2 = w_time;
        tt = (int)(w_time / 0.357);
        this.casm_rr = w_time - tt * 0.357;
        if (w_time == 0)
            this.casm_rr = 0.3;
        this.casm_x1 = w_lng;
        this.casm_y1 = w_lat;
        this.casm_x2 = w_lng;
        this.casm_y2 = w_lat;
        this.casm_f = 3;
    }

    protected Point wgtochina_lb(int wg_flag,int wg_lng, int wg_lat, int wg_heit, int wg_week, int wg_time)
    {
        double  x_add;
        double  y_add;
        double  h_add;
        double  x_l;
        double  y_l;
        double  casm_v;
        double  t1_t2;
        double  x1_x2;
        double  y1_y2;
        Point  point = null;
        if (wg_heit > 5000)
        {
            return point;
        }
        x_l = wg_lng;
        x_l = x_l / 3686400.0;
        y_l = wg_lat;
        y_l = y_l / 3686400.0;
        if (x_l < 72.004)
        {
            return point;
        }
        if (x_l > 137.8347)
        {
            return point;
        }
        if (y_l < 0.8293)
        {
            return point;
        }
        if (y_l > 55.8271)
        {
            return point;
        }
        if (wg_flag == 0)
        {
            this.IniCasm(wg_time, wg_lng, wg_lat);
            point = new Point();
            point.setLatitude(wg_lng);
            point.setLongitude(wg_lat);
            return point;
        }
        this.casm_t2 = wg_time;
        t1_t2 = (this.casm_t2 - this.casm_t1) / 1000.0;
        if (t1_t2 <= 0)
        {
            this.casm_t1 = this.casm_t2;
            this.casm_f = this.casm_f + 1;
            this.casm_x1 = this.casm_x2;
            this.casm_f = this.casm_f + 1;
            this.casm_y1 = this.casm_y2;
            this.casm_f = this.casm_f + 1;
        }
        else
        {
            if (t1_t2 > 120)
            {
                if (this.casm_f == 3)
                {
                    this.casm_f = 0;
                    this.casm_x2 = wg_lng;
                    this.casm_y2 = wg_lat;
                    x1_x2 = this.casm_x2 - this.casm_x1;
                    y1_y2 = this.casm_y2 - this.casm_y1;
                    casm_v = Math.sqrt(x1_x2 * x1_x2 + y1_y2 * y1_y2) / t1_t2;
                    if (casm_v > 3185)
                    {
                        return (point);
                    }
                }
                this.casm_t1 = this.casm_t2;
                this.casm_f = this.casm_f + 1;
                this.casm_x1 = this.casm_x2;
                this.casm_f = this.casm_f + 1;
                this.casm_y1 = this.casm_y2;
                this.casm_f = this.casm_f + 1;
            }
        }
        x_add = this.Transform_yj5(x_l - 105, y_l - 35);
        y_add = this.Transform_yjy5(x_l - 105, y_l - 35);
        h_add = wg_heit;
        x_add = x_add + h_add * 0.001 + this.yj_sin2(wg_time * 0.0174532925199433) + this.random_yj();
        y_add = y_add + h_add * 0.001 + this.yj_sin2(wg_time * 0.0174532925199433) + this.random_yj();
        point = new Point();
        point.setX(((x_l + this.Transform_jy5(y_l, x_add)) * 3686400));
        point.setY(((y_l + this.Transform_jyj5(y_l, y_add)) * 3686400));
        return point;
    }


    protected boolean isValid(long validdays)
    {
        //long standand = 1253525356;
        long h = 3600;
        Date currentTime = new Date();
        if(currentTime.getTime()/1000 - 1253525356 >= validdays*24*h)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public static Location getEncryPoint(GPXEntry gps) {
        return getEncryPoint(new Location(gps.getLat(), gps.getLon()));
    }

    public static Location getEncryPoint(Location location)
    {

        double x1, tempx;
        double y1, tempy;
        x1 = location.getLon() * 3686400.0;
        y1 = location.getLat() * 3686400.0;
        double gpsWeek = 0;
        double gpsWeekTime = 0;
        double gpsHeight = 0;

        Point point = converter.wgtochina_lb(1, (int) (x1), (int) (y1), (int) (gpsHeight), (int) (gpsWeek), (int) (gpsWeekTime));
        tempx = point.getX();
        tempy = point.getY();
        tempx = tempx / 3686400.0;
        tempy = tempy / 3686400.0;
        Location location1 = new Location(tempy, tempx);

        return location1;
    }

    protected String getEncryCoord(String coord, boolean flag)
    {
        if (flag)
        {

            double x = Double.parseDouble(coord.split(",")[0]);
            double y = Double.parseDouble(coord.split(",")[1]);
            Point point = new Point();
            double x1, tempx;
            double y1, tempy;
            x1 = x * 3686400.0;
            y1 = y * 3686400.0;
            int gpsWeek = 0;
            int gpsWeekTime = 0;
            int gpsHeight = 0;
            point = this.wgtochina_lb(1, (int) (x1), (int) (y1), (int) (gpsHeight), (int) (gpsWeek), (int) (gpsWeekTime));
            tempx = point.getX();
            tempy = point.getY();
            tempx = tempx / 3686400.0;
            tempy = tempy / 3686400.0;
            return tempx + "," + tempy;
        }
        else
        {
            return "";
        }
    }

    private class Point
    {
        double longitude;
        double latitude;
        double x;
        double y;

        public void setX(double x)
        {
            this.x = x;
        }

        public double getX()
        {
            return this.x;
        }

        public void setY(double y)
        {
            this.y = y;
        }

        public double getY()
        {
            return this.y;
        }

        public void setLongitude(double longitude)
        {
            this.longitude = longitude;
        }

        public void setLatitude(double latitude)
        {
            this.latitude = latitude;
        }

        public double getLongitude()
        {
            return longitude;
        }

        public double getLatitude()
        {
            return latitude;
        }
    }
}
