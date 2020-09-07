package finki.ukim.mpip.gladensum.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.Locale;

import finki.ukim.mpip.gladensum.classes.Order;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ReverseGeocoderService extends IntentService {

    private static final String DECODE = "finki.ukim.mpip.gladensum.services.action.decode";
    private static final String LAT_LNG_CODE = "latLng";
    private static final String ORDER_CODE="order";

    public ReverseGeocoderService() {
        super("ReverseGeocoderService");
    }

    public static void startDecode(Context context, LatLng latLng, Order o) {
        Intent intent = new Intent(context, ReverseGeocoderService.class);
        intent.setAction(DECODE);
        intent.putExtra(LAT_LNG_CODE, latLng);
        intent.putExtra(ORDER_CODE,o);
        context.startService(intent);
    }





    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (DECODE.equals(action)) {
                LatLng latLng = intent.getParcelableExtra(LAT_LNG_CODE);
                Order order = (Order) intent.getSerializableExtra(ORDER_CODE);
                try {
                    if (latLng != null&&order!=null)
                        handleActionDecode(latLng,order);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public String handleActionDecode(LatLng ll,Order order) throws IOException {
        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);

        String address = geocoder.getFromLocation(ll.latitude, ll.longitude, 1).get(0).getAddressLine(0);
        order.setUserLocation(ll);
        order.setUserAddress(address);
        return address;
    }

}
