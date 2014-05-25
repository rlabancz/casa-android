/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.android;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.android.Contents;
import com.google.zxing.client.android.Intents;
import com.google.zxing.common.BitMatrix;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import java.util.EnumMap;
import java.util.Map;

/**
 * This class does the work of decoding the user's request and extracting all the data to be encoded in a barcode.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 */
final class QRCodeEncoder {

	private static final int WHITE = 0xFFFFFFFF;
	private static final int BLACK = 0xFF000000;

	final Context activity;
	private String contents;
	private String displayContents;
	private BarcodeFormat format;
	private final int dimension;
	private final boolean useVCard;

	QRCodeEncoder(Context activity, Intent intent, int dimension, boolean useVCard) throws WriterException {
		this.activity = activity;
		this.dimension = dimension;
		this.useVCard = useVCard;
		String action = intent.getAction();
		if (action.equals(Intents.Encode.ACTION)) {
			encodeContentsFromZXingIntent(intent);
		}
	}

	String getContents() {
		return contents;
	}

	String getDisplayContents() {
		return displayContents;
	}

	boolean isUseVCard() {
		return useVCard;
	}

	// It would be nice if the string encoding lived in the core ZXing library,
	// but we use platform specific code like PhoneNumberUtils, so it can't.
	private boolean encodeContentsFromZXingIntent(Intent intent) {
		// Default to QR_CODE if no format given.
		String formatString = intent.getStringExtra(Intents.Encode.FORMAT);
		format = null;
		if (formatString != null) {
			try {
				format = BarcodeFormat.valueOf(formatString);
			} catch (IllegalArgumentException iae) {
				// Ignore it then
			}
		}
		if (format == null || format == BarcodeFormat.QR_CODE) {
			String type = intent.getStringExtra(Intents.Encode.TYPE);
			if (type == null || type.isEmpty()) {
				return false;
			}
			this.format = BarcodeFormat.QR_CODE;
			encodeQRCodeContents(intent, type);
		} else {
			String data = intent.getStringExtra(Intents.Encode.DATA);
			if (data != null && !data.isEmpty()) {
				contents = data;
				displayContents = data;
			}
		}
		return contents != null && !contents.isEmpty();
	}

	private void encodeQRCodeContents(Intent intent, String type) {
		switch (type) {
		case Contents.Type.TEXT:
			String data = intent.getStringExtra(Intents.Encode.DATA);
			if (data != null && !data.isEmpty()) {
				contents = data;
				displayContents = data;
			}
			break;
		}
	}

	public Bitmap encodeAsBitmap() throws WriterException {
		String contentsToEncode = contents;
		if (contentsToEncode == null) {
			return null;
		}
		Map<EncodeHintType, Object> hints = null;
		String encoding = guessAppropriateEncoding(contentsToEncode);
		if (encoding != null) {
			hints = new EnumMap<>(EncodeHintType.class);
			hints.put(EncodeHintType.CHARACTER_SET, encoding);
		}
		BitMatrix result;
		try {
			result = new MultiFormatWriter().encode(contentsToEncode, format, dimension, dimension, hints);
		} catch (IllegalArgumentException iae) {
			// Unsupported format
			return null;
		}
		int width = result.getWidth();
		int height = result.getHeight();
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			int offset = y * width;
			for (int x = 0; x < width; x++) {
				pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
			}
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	private static String guessAppropriateEncoding(CharSequence contents) {
		// Very crude at the moment
		for (int i = 0; i < contents.length(); i++) {
			if (contents.charAt(i) > 0xFF) {
				return "UTF-8";
			}
		}
		return null;
	}
}