#include <stdio.h>
#include <stdbool.h>


int di, df, hi, hf, mi, mf, si, sf, dt, ht, mt, st;


int main() {
    di = 5;
    df = 9;
    hi = 2;
    hf = 5;
    mi = 3;
    mf = 1;
    si = 10;
    sf = 37;
    dt = df - di;
    ht = hf - hi;
    mt = mf - mi;
    st = sf - si;
    if (ht < 0) {
        ht = hi - hf;
        dt = dt - 1;
    }
    if (mt < 0) {
        mt = 60 - mi + mf;
        ht = ht - 1;
    }
    if (st < 0) {
        st = 60 - si + sf;
        mt = mt - 1;
    }
}
