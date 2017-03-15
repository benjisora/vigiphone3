package com.cstb.vigiphone3.data.model;

/**
 * Lambert class, used to translate GPS coordinates in an orthonormal coordinates system
 */
public class Lambert {

    public static final Double M_PI = 3.14159265358979323846;
    public static final Double M_PI_2 = 1.57079632679489661923;
    public static final Double M_PI_4 = 0.78539816339744830962;
    public static Double LAMBDA0 = M_PI * 3.0 / 180.0;
    protected Double m_e, m_c, m_n, m_Xs, m_Ys, m_lambdac;

    /**
     * Alternative constructor with default value
     */
    public Lambert(Double e, Double c, Double n, Double Xs, Double Ys) {
        this(e, c, n, Xs, Ys, LAMBDA0);
    }

    /**
     * Initializes each value
     * @param e
     * @param c
     * @param n
     * @param Xs
     * @param Ys
     * @param lambdac
     */
    public Lambert(Double e, Double c, Double n, Double Xs, Double Ys, Double lambdac) {
        this.m_e = e;
        this.m_c = c;
        this.m_n = n;
        this.m_Xs = Xs;
        this.m_Ys = Ys;
        this.m_lambdac = lambdac;
    }

    /**
     *
     * @param phi
     * @return
     */
    public Double L(Double phi) {
        Double e_sinphi = m_e * Math.sin(phi);

        return Math.log(Math.tan(M_PI_4 + 0.5 * phi) * Math.pow((1.0 - e_sinphi) / (1.0 + e_sinphi), 0.5 * m_e));
    }

    /**
     *
     * @param L
     * @param epsilon
     * @return
     */
    public Double Linv(Double L, Double epsilon) {
        Double expL = Math.exp(L),
                phi0,
                phi1 = 2.0 * Math.atan(expL) - M_PI_2;

        do {
            Double e_sinphi = m_e * Math.sin(phi1);

            phi0 = phi1;
            phi1 = 2.0 * Math.atan(Math.pow((1.0 + e_sinphi) / (1.0 - e_sinphi), 0.5 * m_e) * expL) - M_PI_2;
        }
        while (Math.abs((phi1 - phi0)) > epsilon);

        return phi1;
    }

    /**
     *
     * @param lambda
     * @param phi
     * @param XY
     */
    public void XY(Double lambda, Double phi, Double[] XY) {
        Double u = this.m_c * Math.exp(-this.m_n * L(phi)),
                v = this.m_n * (lambda - this.m_lambdac);

        XY[0] = this.m_Xs + u * Math.sin(v);
        XY[1] = this.m_Ys - u * Math.cos(v);
    }

    /**
     * Alternative method with default value
     */
    public void XYinv(Double X, Double Y, Double[] lambdaPhi) {
        XYinv(X, Y, lambdaPhi, 1.0e-11);
    }

    /**
     *
     * @param X
     * @param Y
     * @param lambdaPhi
     * @param epsilon
     */
    public void XYinv(Double X, Double Y, Double[] lambdaPhi, Double epsilon) {

        double dX = X - this.m_Xs,
                dY = Y - this.m_Ys,
                R = Math.sqrt(dX * dX + dY * dY),
                v = Math.atan(-dX / dY);

        lambdaPhi[0] = this.m_lambdac + v / this.m_n;
        lambdaPhi[1] = Linv(-Math.log(Math.abs(R / this.m_c)) / this.m_n, epsilon);

    }

    /**
     * Lambert93 class, with default values
     */
    public static class Lambert93 extends Lambert {

        public Lambert93() {
            super(0.0818191910428,
                    11754255.426,
                    0.7256077650,
                    700000.0,
                    12655612.050);
        }

    }


}