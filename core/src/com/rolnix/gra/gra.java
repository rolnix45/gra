package com.rolnix.gra;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.Iterator;

public class gra extends ApplicationAdapter {

	public enum State{
		RUNNING,
		PAUSE,
		START,
		ACHIEVEMENT,
		KONIEC,
		STATYSTYKI,
	} State stan;

	private FileHandle zapis;

	private OrthographicCamera kamerka;
	private OrthographicCamera hudKamerka;

	private SpriteBatch zadanie;
	private Rectangle ty;
	private Texture fota;
	private Texture cosTekstura;
	private Texture przyciemnienie;
	private Texture pociskTextura;
	private Texture tlo;
	private Texture boostTekstura;
	private Array<Rectangle> kilkaCos;
	private Array<Rectangle> kilkaPocisk;
	private Array<Rectangle> kilkaBoost;

	private BitmapFont malyTekst;
	private BitmapFont duzyTekst;
	private BitmapFont achTekst;
	private BitmapFont tekst;
	private BitmapFont wyjatek;

	private FreeTypeFontGenerator generator;

	private String trybSlowo;
	private String wyjatekTekst;

	private boolean ach1 = false;
	private boolean ach2 = false;
	private boolean ach3 = false;
	private boolean ach4 = false;

	private Sound wybor;
	private Sound umarcie;
	private Sound strzal;
	private Sound bum;
	private Sound clear;
	private Sound ammoDzwiek;
	private Music tloMuzyka;

	private ParticleEffect wybuchCos;
	private ParticleEffect bigWybuch;

	private long tak;
	private long czasOdOstatnieCos;
	private long czasOdOstatnieAmmo;
	private long czasOdOstatnieBoost;
	private long boostTimer;
	private long czasTeraz;
	private long poczatekCzas;
	private long licznik;
	private int punkty;
	private int mnoznik;
	private int szypkosc;
	private int predkoscStrzalu;
	private int ammo;
	private byte tryb;

	private Boolean drawBoostText;

	private Staty staty;
	private Boost boost;

	@Override
	public void create () {
		fota = new Texture(Gdx.files.internal("textures/fota.png"));
		cosTekstura = new Texture(Gdx.files.internal("textures/cos.png"));
		przyciemnienie = new Texture(Gdx.files.internal("textures/przyciemnienie.png"));
		pociskTextura = new Texture(Gdx.files.internal("textures/pocisk.png"));
		boostTekstura = new Texture(Gdx.files.internal("textures/boost.png"));
		tlo = new Texture(Gdx.files.internal("textures/tlo.png"));

		malyTekst = new BitmapFont(Gdx.files.internal("font/male.fnt"));
		duzyTekst = new BitmapFont(Gdx.files.internal("font/czcionka.fnt"));
		achTekst = new BitmapFont(Gdx.files.internal("font/czcionka.fnt"));
		tekst = new BitmapFont(Gdx.files.internal("font/czcionka.fnt"));

		generator = new FreeTypeFontGenerator(Gdx.files.internal("font/czcionka.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 28;
		parameter.color = Color.RED;
		wyjatek = generator.generateFont(parameter);

		duzyTekst.getData().setScale(3.0f);
		achTekst.getData().setScale(1.5f);

		zapis = Gdx.files.local("./save.rff");
		boolean czyIstniejeZapis = Gdx.files.local("./save.rff").exists();

		wybor = Gdx.audio.newSound(Gdx.files.internal("sounds/klikniecie.wav"));
		umarcie = Gdx.audio.newSound(Gdx.files.internal("sounds/umarcie.wav"));
		strzal = Gdx.audio.newSound(Gdx.files.internal("sounds/strzal.wav"));
		bum = Gdx.audio.newSound((Gdx.files.internal("sounds/bum2.wav")));
		clear = Gdx.audio.newSound((Gdx.files.internal("sounds/clear.wav")));
		ammoDzwiek = Gdx.audio.newSound((Gdx.files.internal("sounds/ammo.wav")));
		tloMuzyka = Gdx.audio.newMusic(Gdx.files.internal("sounds/muzyka.wav"));

		wybuchCos = new ParticleEffect();
		wybuchCos.load(Gdx.files.internal("particles/wybuchCos"), Gdx.files.internal("particles"));
		bigWybuch = new ParticleEffect();
		bigWybuch.load(Gdx.files.internal("particles/bigBoom"), Gdx.files.internal("particles"));

		tloMuzyka.setLooping(true);
		tloMuzyka.setVolume(0.4f);

		kamerka = new OrthographicCamera();
		kamerka.setToOrtho(false, 1366, 768);

		hudKamerka = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		hudKamerka.position.set(hudKamerka.viewportWidth / 2.0f, hudKamerka.viewportHeight / 2.0f, 1.0f);

		stan = State.START;

		zadanie = new SpriteBatch();

		wyjatekTekst = "";

		czasTeraz = 0;
		mnoznik = 1000000;
		tak = 1000000000;
		trybSlowo = "";

		predkoscStrzalu = 500;
		ammo = 1;
		punkty = 0;

		drawBoostText = false;

		ty = new Rectangle();
		ty.x = (1366 / 2f) - (80f / 2f);
		ty.y = 20;
		ty.width = 80;
		ty.height = 80 - 28;

		kilkaCos = new Array<>();
		kilkaPocisk = new Array<>();
		kilkaBoost = new Array<>();

		staty = new Staty();
		boost = new Boost();

		if (czyIstniejeZapis) { wczytaj(); }
	}

	private void spawnCos() {
		Rectangle cos = new Rectangle();
		cos.x = MathUtils.random(0, 1366 - 64);
		cos.y = 768 + 64;
		cos.width = 64;
		cos.height = 64;
		kilkaCos.add(cos);
		czasOdOstatnieCos = TimeUtils.nanoTime();
	}

	private void spawnPocisk() {
		Rectangle pocisk = new Rectangle();
		pocisk.x = ty.x + 40;
		pocisk.y = ty.y + 26;
		pocisk.width = 32;
		pocisk.height = 32;
		kilkaPocisk.add(pocisk);
		strzal.play();
	}

	public void spawnBoost() {
		Rectangle boost = new Rectangle();
		boost.x = MathUtils.random(0, 1366 - 64);
		boost.y = 768 + 64;
		boost.width = 64;
		boost.height = 64;
		kilkaBoost.add(boost);
		czasOdOstatnieBoost = TimeUtils.millis();
	}

	@Override
	public void render () {
		switch (stan) {
			case RUNNING:
				update();
				break;
			case START:
				menu();
				break;
			case ACHIEVEMENT:
				achivementyPetla();
				break;
			case STATYSTYKI:
				statyPetla();
				break;
			case PAUSE:
				pauzaPetla();
				break;
			case KONIEC:
				koniecPetla();
				break;
		} draw();
	}

	private void update() {
		//bindy i movement
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) ty.x -= 200 * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) ty.x += 200 * Gdx.graphics.getDeltaTime();

		if (Gdx.input.isKeyJustPressed(Input.Keys.X) && ammo > 0) {
			spawnPocisk();
			staty.setStrzaly(staty.getStrzaly() + 1);
			ammo--;
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && stan == State.RUNNING) pauzaRaz();

		//blokuje zeby gracz nie wyszedl za ekran
		if (ty.x < 0) ty.x = 0;
		if (ty.x > 1366 - 80) ty.x = 1366 - 80;

		//spawnuje cos
		if (TimeUtils.nanoTime() - czasOdOstatnieCos > tak) spawnCos();

		//dodaje ammo co 15 sek
		if (TimeUtils.millis() - czasOdOstatnieAmmo > 15000) {
			ammo++;
			czasOdOstatnieAmmo = TimeUtils.millis();
		}

		if (TimeUtils.millis() - czasOdOstatnieBoost > 100) {
			if (MathUtils.random(0, 10000) <= TimeUtils.millis() - czasOdOstatnieBoost)
				spawnBoost();
			czasOdOstatnieBoost = TimeUtils.millis();
		}

		if (drawBoostText) {
			if (TimeUtils.millis() - boostTimer >= 1000) {
				drawBoostText = false;
				boostTimer = TimeUtils.millis();
			}
		}

		//poziomy trudnosci
		tak -= mnoznik;
		switch (tryb) {
			//latwy
			case 0:
				szypkosc = 200;
				if (tak < 750000000) mnoznik = 200000;
				if (tak < 333333333) { mnoznik = 300000; szypkosc = 215; }
				if (tak < 250000000) { mnoznik = 0;  szypkosc = 225; }
				if (punkty == 150) ach1 = true;
				break;

			//sredni
			case 1:
				szypkosc = 225;
				if (tak < 850000000) mnoznik = 200000;
				if (tak < 666666667) { mnoznik = 300000; szypkosc = 240; }
				if (tak < 400000000) { mnoznik = 400000; szypkosc = 250; }
				if (tak < 150000000) mnoznik = 0;
				if (punkty == 175) ach2 = true;
				break;

			//trudny
			case 2:
				szypkosc = 250;
				if (tak < 850000000) mnoznik = 250000;
				if (tak < 666666667) { mnoznik = 350000; szypkosc = 265; }
				if (tak < 400000000) { mnoznik = 450000; szypkosc = 275;}
				if (tak < 100000000) mnoznik = 0;
				if (punkty == 200) ach3 = true;
				break;
		}

		if (punkty == 200 && ach1 && ach2 && ach3) ach4 = true;

		//liczy czas
		czasTeraz = TimeUtils.millis();
		licznik = (czasTeraz / 1000) - (poczatekCzas / 1000);

		//sprawdza kolizje i niszczy cos
		for (Iterator<Rectangle> iter = new Array.ArrayIterator<>(kilkaCos).iterator(); iter.hasNext();) {
			Rectangle cos = iter.next();
			cos.y -= szypkosc * Gdx.graphics.getDeltaTime();
			if (cos.y + 64 < 0) { iter.remove(); punkty++; }
			if (cos.overlaps(ty)) { koniecRaz(); }
		}

		for (Iterator<Rectangle> iter = new Array.ArrayIterator<>(kilkaPocisk).iterator(); iter.hasNext();) {
			Rectangle pocisk = iter.next();
			pocisk.y += predkoscStrzalu * Gdx.graphics.getDeltaTime();
			if (pocisk.y > 512) { iter.remove(); }
			for (Iterator<Rectangle> iter2 = new Array.ArrayIterator<>(kilkaCos).iterator(); iter2.hasNext();) {
				Rectangle cos = iter2.next();
				if (pocisk.overlaps(cos)) {
					iter2.remove(); iter.remove();
					wybuchCos.getEmitters().first().setPosition(cos.x, cos.y);
					wybuchCos.start();
					bum.play();
				}
			}
		}

		for (Iterator<Rectangle> iter = new Array.ArrayIterator<>(kilkaBoost).iterator(); iter.hasNext();) {
			Rectangle boostObj = iter.next();
			boostObj.y -= szypkosc * Gdx.graphics.getDeltaTime();
			if (boostObj.y + 64 < 0) { iter.remove(); }
			if (boostObj.overlaps(ty)) {
				boost.setBoost(MathUtils.random(0, 1));
				iter.remove();
				dajBoost();
			}
		}

		wybuchCos.update(Gdx.graphics.getDeltaTime());
		bigWybuch.update(Gdx.graphics.getDeltaTime());

		zapis();
	}


	private void draw() {
		ScreenUtils.clear(0, 0, 0, 0);

		kamerka.update();
		zadanie.setProjectionMatrix(kamerka.combined);

		zadanie.begin();

		zadanie.draw(tlo, 0, 0);

		switch (stan) {
			case RUNNING:
				for (Rectangle cos: new Array.ArrayIterator<>(kilkaCos)) zadanie.draw(cosTekstura, cos.x, cos.y);
				for (Rectangle pocisk: new Array.ArrayIterator<>(kilkaPocisk)) zadanie.draw(pociskTextura, pocisk.x, pocisk.y);
				for (Rectangle boost: new Array.ArrayIterator<>(kilkaBoost)) zadanie.draw(boostTekstura, boost.x, boost.y);

				if (drawBoostText)
					duzyTekst.draw(zadanie, boost.getBoostName(), (hudKamerka.viewportWidth / 2.0f) - 500, (hudKamerka.viewportHeight / 1.5f));

				tekst.draw(zadanie, "AMMO: " + ammo, 1366.f / 2, hudKamerka.viewportHeight - 1);
				zadanie.draw(fota, ty.x, ty.y);
				wybuchCos.draw(zadanie);
				bigWybuch.draw(zadanie);
				break;

			case START:
				achTekst.draw(zadanie,
						"F1 - LATWY" + System.lineSeparator() + "F2 - SREDNI" + System.lineSeparator() +
								"F3 - TRUDNY" + System.lineSeparator() + "F4 - ACHIEVEMENTY" +
								System.lineSeparator() + "F5 - STATYSTYKI",
						hudKamerka.viewportWidth / 2.0f, hudKamerka.viewportHeight / 2.0f);
				break;

			case ACHIEVEMENT:
				tekst.draw(zadanie, "ESC - wyjscie", 2.0f, hudKamerka.viewportHeight - 35);
				if (ach1) { achTekst.draw(zadanie, "150 PUNKTOW NA LATWYM",
						(hudKamerka.viewportWidth / 2.0f) - 500, (hudKamerka.viewportHeight / 2.0f) + 120); }
				if (ach2) { achTekst.draw(zadanie, "175 PUNKTOW NA SREDNIM",
						(hudKamerka.viewportWidth / 2.0f) - 500, (hudKamerka.viewportHeight / 2.0f) + 60); }
				if (ach3) { achTekst.draw(zadanie, "200 PUNKTOW NA TRUDNYM",
						(hudKamerka.viewportWidth / 2.0f) - 500, (hudKamerka.viewportHeight / 2.0f)); }
				if (ach4) { achTekst.draw(zadanie, "WSZYSTKIE ACHIEVEMENTY + 200 NA TRUDNYM ZNOWU",
						(hudKamerka.viewportWidth / 2.0f) - 500, (hudKamerka.viewportHeight / 2.0f) - 60); }
				break;

			case PAUSE:
				for (Rectangle cos: new Array.ArrayIterator<>(kilkaCos)) zadanie.draw(cosTekstura, cos.x, cos.y);
				for (Rectangle pocisk: new Array.ArrayIterator<>(kilkaPocisk)) zadanie.draw(pociskTextura, pocisk.x, pocisk.y);
				for (Rectangle boost: new Array.ArrayIterator<>(kilkaBoost)) zadanie.draw(boostTekstura, boost.x, boost.y);
				zadanie.draw(fota, ty.x, ty.y);
				wybuchCos.draw(zadanie);
				bigWybuch.draw(zadanie);
				zadanie.draw(przyciemnienie, 0, 0);
				duzyTekst.draw(zadanie, "PAUZA", (hudKamerka.viewportWidth - 100) / 2.0f, hudKamerka.viewportHeight / 2.0f);
				tekst.draw(zadanie, "BACKSPACE - RESET",  2.0f, hudKamerka.viewportHeight - 35);
				break;

			case KONIEC:
				for (Rectangle cos: new Array.ArrayIterator<>(kilkaCos)) zadanie.draw(cosTekstura, cos.x, cos.y);
				for (Rectangle pocisk: new Array.ArrayIterator<>(kilkaPocisk)) zadanie.draw(pociskTextura, pocisk.x, pocisk.y);
				for (Rectangle boost: new Array.ArrayIterator<>(kilkaBoost)) zadanie.draw(boostTekstura, boost.x, boost.y);
				zadanie.draw(fota, ty.x, ty.y);
				wybuchCos.draw(zadanie);
				bigWybuch.draw(zadanie);
				duzyTekst.draw(zadanie, "UMARES", (hudKamerka.viewportWidth - 100) / 2.0f, hudKamerka.viewportHeight / 2.0f);
				tekst.draw(zadanie, "BACKSPACE - RESET",  2.0f, hudKamerka.viewportHeight - 35);
				break;

			case STATYSTYKI:
				tekst.draw(zadanie, "ESC - wyjscie", 2.0f, hudKamerka.viewportHeight - 35);
				achTekst.draw(zadanie, "STRZALY: " + staty.getStrzaly() + System.lineSeparator()
								         + "PUNKTY: " + staty.getPunktyRazem() + System.lineSeparator()
										 + "SMIERCI: " + staty.getSmierci()
						, (hudKamerka.viewportWidth / 2.0f) - 500, (hudKamerka.viewportHeight / 1.5f));
				break;

		}
		//zawsze
		tekst.draw(zadanie, "" + Gdx.graphics.getFramesPerSecond(), 2.0f, hudKamerka.viewportHeight - 2);

		tekst.draw(zadanie, "PUNKTY: " + punkty, 2.0f, 25.0f);
		tekst.draw(zadanie, "CZAS: " + licznik, 2.0f, 50.0f);

		malyTekst.draw(zadanie, "v1.6", hudKamerka.viewportWidth - 50, 30);
		malyTekst.draw(zadanie, "copyright by rolnix", hudKamerka.viewportWidth - 244, 15);

		tekst.draw(zadanie, trybSlowo, hudKamerka.viewportWidth - 120, hudKamerka.viewportHeight - 2);

		wyjatek.draw(zadanie, wyjatekTekst, 2.0f, hudKamerka.viewportHeight - 2);

		zadanie.end();
	}

	private void menu() {
		if (Gdx.input.isKeyPressed(Input.Keys.F1)) {
			rozpocznij((byte) 0, "LATWY"); }
		if (Gdx.input.isKeyPressed(Input.Keys.F2)) {
			rozpocznij((byte) 1, "SREDNI"); }
		if (Gdx.input.isKeyPressed(Input.Keys.F3)) {
			rozpocznij((byte) 2, "TRUDNY"); }
		if (Gdx.input.isKeyPressed(Input.Keys.F4)) { achievementRaz(); }
		if (Gdx.input.isKeyPressed(Input.Keys.F5)) { statyRaz(); }
	}

	private void rozpocznij(byte wybrane, String trybNapis) {
		poczatekCzas = TimeUtils.millis();
		czasOdOstatnieAmmo = TimeUtils.millis();
		boostTimer = TimeUtils.millis();
		czasOdOstatnieBoost = TimeUtils.millis();
		tryb = wybrane;
		trybSlowo = trybNapis;
		stan = State.RUNNING;
		wybor.play();
		tloMuzyka.play();
	}
	private void dajBoost() {
		drawBoostText = true;
		boostTimer = TimeUtils.millis();
		if (boost.getBoostType() == Boost.BoostType.ammo) {
			ammo += 3;
			ammoDzwiek.play();
		} else if (boost.getBoostType() == Boost.BoostType.clear) {
			for (Iterator<Rectangle> iter = new Array.ArrayIterator<>(kilkaCos).iterator(); iter.hasNext();) {
				iter.next();
				bigWybuch.getEmitters().first().setPosition(kamerka.viewportWidth / 2, kamerka.viewportHeight / 2);
				bigWybuch.start();
				iter.remove();
			}
			clear.play();
		}
	}

	//pauza
	private void pauzaPetla() {
		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			stan = State.RUNNING;
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
			staty.setPunktyRazem(staty.getPunktyRazem() + punkty);
			zapis();
			tloMuzyka.stop();
			create();
		}
	}
	private void pauzaRaz() {
		stan = State.PAUSE;
	}

	//achievementy
	private void achivementyPetla() {
		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			stan = State.START;
			wybor.play();
		}
	}
	private void achievementRaz() {
		stan = State.ACHIEVEMENT;
		wybor.play();
	}

	//staty
	private void statyPetla() {
		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			stan = State.START;
			wybor.play();
		}
	}
	private void statyRaz() {
		stan = State.STATYSTYKI;
		wybor.play();
	}

	//umarcie
	private void koniecPetla() {
		if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
			tloMuzyka.stop();
			create();
		}
	}
	private void koniecRaz() {
		fota = new Texture(Gdx.files.internal("textures/ded.png"));
		staty.setPunktyRazem(staty.getPunktyRazem() + punkty);
		staty.setSmierci(staty.getSmierci() + 1);
		zapis();
		stan = State.KONIEC;
		tloMuzyka.stop();
		umarcie.play();
	}

	private void zapis() {
		try {
			zapis.writeString(ach1 + "/" + ach2 + "/" + ach3 + "/" + ach4 + '/'
					+ staty.getStrzaly() + '/' + staty.getPunktyRazem() + '/' + staty.getSmierci(), false);
		} catch (Exception e) {
			wyjatekTekst = "BLAD ZAPISU: " + System.lineSeparator() + e;
			System.out.println(wyjatekTekst);
		}
	}

	private void wczytaj() {
		String wczytane = zapis.readString();
		String[] gotowyOdczyt = wczytane.split("/");

		ach1 = Boolean.parseBoolean(gotowyOdczyt[0]);
		ach2 = Boolean.parseBoolean(gotowyOdczyt[1]);
		ach3 = Boolean.parseBoolean(gotowyOdczyt[2]);
		ach4 = Boolean.parseBoolean(gotowyOdczyt[3]);

		staty.setStrzaly(Integer.parseInt(gotowyOdczyt[4]));
		staty.setPunktyRazem(Integer.parseInt(gotowyOdczyt[5]));
		staty.setSmierci(Integer.parseInt(gotowyOdczyt[6]));
	}

	@Override
	public void dispose () {
		zadanie.dispose();
		cosTekstura.dispose();
		fota.dispose();
		przyciemnienie.dispose();
		pociskTextura.dispose();
		tlo.dispose();
		boostTekstura.dispose();

		wybuchCos.dispose();
		bigWybuch.dispose();

		malyTekst.dispose();
		duzyTekst.dispose();
		achTekst.dispose();
		tekst.dispose();
		wyjatek.dispose();

		generator.dispose();

		wybor.dispose();
		umarcie.dispose();
		bum.dispose();
		strzal.dispose();
		ammoDzwiek.dispose();
		clear.dispose();
		tloMuzyka.dispose();
	}
}