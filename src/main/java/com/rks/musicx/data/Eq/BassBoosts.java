package com.rks.musicx.data.eq;

import android.content.SharedPreferences;
import android.media.audiofx.BassBoost;
import android.util.Log;

import com.rks.musicx.misc.utils.Extras;

import static com.rks.musicx.misc.utils.Constants.BASSBOOST_STRENGTH;
import static com.rks.musicx.misc.utils.Constants.BASS_BOOST;

/*
 * Created by Coolalien on 06/01/2017.
 */

/*
 * ©2017 Rajneesh Singh
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class BassBoosts {

    private static BassBoost bassBoost = null;
    private static short str = -1;

    public BassBoosts() {
    }

    public static void initBass(int audioID) {
        EndBass();
        try {
            bassBoost = new BassBoost(0, audioID);
            short str = (short) Extras.getInstance().saveEq().getInt(BASS_BOOST, 0);
            if (str != 0) {
                if (bassBoost != null && bassBoost.getStrengthSupported()) {
                    bassBoost.setStrength(str);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void EndBass() {
        if (bassBoost != null) {
            bassBoost.release();
            bassBoost = null;
        }
    }

    public static void setBassBoostStrength(short strength) {
        str = strength;
        if (bassBoost != null && bassBoost.getStrengthSupported()) {
            try {
                if (str != -1 && str <= BASSBOOST_STRENGTH) {
                    bassBoost.setStrength(strength);
                } else {
                    bassBoost.setStrength((short) 0);
                }
            } catch (IllegalArgumentException e) {
                Log.e("BassBoosts", "Bassboost effect not supported");
            } catch (IllegalStateException e) {
                Log.e("BassBoosts", "Bassboost cannot get strength supported");
            } catch (UnsupportedOperationException e) {
                Log.e("BassBoosts", "Bassboost library not loaded");
            } catch (RuntimeException e) {
                Log.e("BassBoosts", "Bassboost effect not found");
            }
            saveBass();
        }
    }

    public static void initBassBoostValues() {
        str = (short) Extras.getInstance().saveEq().getInt(BASS_BOOST, 0);
    }

    public static void saveBass() {
        if (bassBoost == null) {
            return;
        }
        SharedPreferences.Editor editor = Extras.getInstance().saveEq().edit();
        editor.putInt(BASS_BOOST, str);
        editor.apply();
    }

    public static short getStr() {
        return str;
    }


    public static void setEnabled(boolean enabled) {
        if (bassBoost != null) {
            bassBoost.setEnabled(enabled);
        }
    }

    public static short getRounded() {
        if (bassBoost == null) {
            return 0;
        }
        return bassBoost.getRoundedStrength();
    }
}
