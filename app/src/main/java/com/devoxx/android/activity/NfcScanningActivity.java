package com.devoxx.android.activity;

import com.devoxx.utils.Logger;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.devoxx.data.Settings_;
import com.devoxx.R;

@EActivity(R.layout.activity_register_nfc)
public class NfcScanningActivity extends BaseActivity {

    // TODO Handle proper user_id from id etc.
    // TODO Probably this activity should be started for result from place where voting occurs.

    @ViewById(R.id.registerNfcLabel)
    TextView label;

    @Pref
    Settings_ settings;

    private NfcAdapter nfcAdapter;
    private IntentFilter[] intentFilters;
    private PendingIntent pendingIntent;

    @AfterViews
    void afterViews() {
        super.afterViews();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP), 0);
        IntentFilter discovery = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        intentFilters = new IntentFilter[]{discovery};
    }

    @Override
    public void onNewIntent(Intent intent) {
        final Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Logger.l("onNewIntent: " + intent + ", tag: " + tag);
        final Ndef ndef = Ndef.get(tag);
        try {
            ndef.connect();
            Logger.l("ndefType: " + ndef.getType());
            final NdefMessage ndefMessage = ndef.getNdefMessage();
            Logger.l("ndefMessage: " + ndefMessage.toString());
            final NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord record : records) {
                Logger.l("record.mimetype: " + record.toMimeType());
                final String payload = readText(record);
                Logger.l("record.payload: " + payload);
                label.setText(payload);

                // TODO Validate user_id and save it for later use, enable voting.
                settings.userId().put(payload);
            }
        } catch (IOException | FormatException e) {
            Logger.l("Can't connect!");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (nfcAdapter != null)
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (nfcAdapter != null)
            nfcAdapter.disableForegroundDispatch(this);
    }

    private static String readText(NdefRecord record) throws UnsupportedEncodingException {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */

        byte[] payload = record.getPayload();

        // Get the Text Encoding
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

        // Get the Language Code
        int languageCodeLength = payload[0] & 0063;

        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
        // e.g. "en"

        // Get the Text
        return new String(payload, languageCodeLength + 1,
                payload.length - languageCodeLength - 1, textEncoding);
    }
}
