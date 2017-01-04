package org.xdty.phone.number.net.soguo;

import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.NumberHandler;
import org.xdty.phone.number.util.App;
import org.xdty.phone.number.util.OkHttp;

import javax.inject.Inject;

public class SogouNumberHandler implements NumberHandler<SogouNumber> {

    @Inject OkHttp mOkHttp;

    public SogouNumberHandler() {
        App.getAppComponent().inject(this);
    }

    @Override
    public String url() {
        return "http://data.haoma.sogou.com/vrapi/query_number.php?type=json&callback=show&number=";
    }

    @Override
    public String key() {
        return null;
    }

    @Override
    public SogouNumber find(String number) {
        String url = url() + number;
        SogouNumber sogouNumber = mOkHttp.get(url, SogouNumber.class);
        sogouNumber.number = number;
        return sogouNumber;
    }

    @Override
    public boolean isOnline() {
        return true;
    }

    @Override
    public int getApiId() {
        return INumber.API_ID_SG;
    }
}
