package com.oubeichen.weather;

import java.util.List;

/**
 * Created by oubeichen on 2014/12/28 0028.
 */
public class Alarm {
    String name;
    Boolean enabled;
    List<Cond> conds;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<Cond> getConds() {
        return conds;
    }

    public void setConds(List<Cond> conds) {
        this.conds = conds;
    }

    public class Cond {
        int opt1;
        int opt2;
        int opt3;
        int opt4;

        public int getOpt1() {
            return opt1;
        }

        public void setOpt1(int opt1) {
            this.opt1 = opt1;
        }

        public int getOpt2() {
            return opt2;
        }

        public void setOpt2(int opt2) {
            this.opt2 = opt2;
        }

        public int getOpt3() {
            return opt3;
        }

        public void setOpt3(int opt3) {
            this.opt3 = opt3;
        }

        public int getOpt4() {
            return opt4;
        }

        public void setOpt4(int opt4) {
            this.opt4 = opt4;
        }
    }
}
