package com.rolnix.gra;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.Iterator;

public class gra extends ApplicationAdapter {

	public enum State{ RUNNING, PAUSE, START, ACHIEVEMENT, KONIEC }
	State stan = State.START;

	private FileHandle zapis;

	private OrthographicCamera kamerka;
	private OrthographicCamera hudKamerka;

	private SpriteBatch zadanie;
	private Rectangle ty;
	private Texture fota;
	private Texture cosTekstura;
	private Texture przyciemnienie;
	private Array<Rectangle> kilkaCos;

	private BitmapFont fps;
	private BitmapFont koniec;
	private BitmapFont czaser;
	private BitmapFont punkter;
	private BitmapFont tryber;
	private BitmapFont wybieracz;
	private BitmapFont wersjaTekst;
	private BitmapFont twurca;
	private BitmapFont pauza;
	private BitmapFont achievementWyjcie;
	private BitmapFont achTekst1;
	private BitmapFont achTekst2;
	private BitmapFont achTekst3;
	private BitmapFont achTekst4;
	private String trybSlowo;

	private boolean ach1;
	private boolean ach2;
	private boolean ach3;
	private boolean ach4;

	private Sound wybor;
	private Sound umarcie;
	private Music tlo;

	private long tak;
	private long czasOdOstatnieCos;
	private long poczatekCzas;
	private long licznik;
	private int punkty;
	private int mnoznik;
	private int szypkosc;
	private byte tryb;

	@Override
	public void create () {
		fota = new Texture(Gdx.files.internal("textures/fota.png"));
		cosTekstura = new Texture(Gdx.files.internal("textures/cos.png"));
		przyciemnienie = new Texture(Gdx.files.internal("textures/przyciemnienie.png"));

		wybieracz = new BitmapFont(Gdx.files.internal("font/dosvga437.fnt"));
		czaser = new BitmapFont(Gdx.files.internal("font/dosvga437.fnt"));
		tryber = new BitmapFont(Gdx.files.internal("font/dosvga437.fnt"));
		fps = new BitmapFont(Gdx.files.internal("font/dosvga437.fnt"));
		punkter = new BitmapFont(Gdx.files.internal("font/dosvga437.fnt"));
		wersjaTekst = new BitmapFont(Gdx.files.internal("font/dosvga437.fnt"));
		twurca = new BitmapFont(Gdx.files.internal("font/dosvga437.fnt"));
		koniec = new BitmapFont(Gdx.files.internal("font/dosvga437.fnt"));
		pauza = new BitmapFont(Gdx.files.internal("font/dosvga437.fnt"));
		achievementWyjcie = new BitmapFont(Gdx.files.internal("font/dosvga437.fnt"));
		achTekst1 = new BitmapFont(Gdx.files.internal("font/dosvga437.fnt"));
		achTekst2 = new BitmapFont(Gdx.files.internal("font/dosvga437.fnt"));
		achTekst3 = new BitmapFont(Gdx.files.internal("font/dosvga437.fnt"));
		achTekst4 = new BitmapFont(Gdx.files.internal("font/dosvga437.fnt"));

		zapis = Gdx.files.local("/save.rff");
		boolean czyIstniejeZapis = Gdx.files.local("/save.rff").exists();

		twurca.getData().setScale(0.5f);
		wersjaTekst.getData().setScale(0.5f);
		koniec.getData().setScale(3.0f);
		pauza.getData().setScale(3.0f);
		achTekst1.getData().setScale(1.5f);
		achTekst2.getData().setScale(1.5f);
		achTekst3.getData().setScale(1.5f);
		achTekst4.getData().setScale(1.5f);

		wybor = Gdx.audio.newSound(Gdx.files.internal("sounds/klikniecie.wav"));
		umarcie = Gdx.audio.newSound(Gdx.files.internal("sounds/umarcie.wav"));
		tlo = Gdx.audio.newMusic(Gdx.files.internal("sounds/muzyka.wav"));

		tlo.setLooping(true);
		tlo.setVolume(0.4f);

		kamerka = new OrthographicCamera();
		kamerka.setToOrtho(false, 1366, 768);

		hudKamerka = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		hudKamerka.position.set(hudKamerka.viewportWidth / 2.0f, hudKamerka.viewportHeight / 2.0f, 1.0f);

		zadanie = new SpriteBatch();

		mnoznik = 1000000;
		tak = 1000000000;
		trybSlowo = "";

		ty = new Rectangle();
		ty.x = (1366 / 2f) - (80f / 2f);
		ty.y = 20;
		ty.width = 80;
		ty.height = 80 - 28;

		kilkaCos = new Array<>();

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
			case PAUSE:
				pauzaPetla();
				break;
			case KONIEC:
				break;
		} draw();
		//System.out.println(stan);
	}

	public void update() {
		//bindy i movement
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) ty.x -= 200 * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) ty.x += 200 * Gdx.graphics.getDeltaTime();

		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && stan == State.RUNNING) stan = State.PAUSE;

		//blokuje zeby gracz nie wyszedl za ekran
		if (ty.x < 0) ty.x = 0;
		if (ty.x > 1366 - 80) ty.x = 1366 - 80;

		//spawnuje cos
		if (TimeUtils.nanoTime() - czasOdOstatnieCos > tak) spawnCos();

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
		long minietyCzas = TimeUtils.millis() / 1000;
		licznik = minietyCzas - poczatekCzas;

		//sprawdza kolizje i niszczy cos
		for (Iterator<Rectangle> iter = new Array.ArrayIterator<>(kilkaCos).iterator(); iter.hasNext();) {
			Rectangle cos = iter.next();
			cos.y -= szypkosc * Gdx.graphics.getDeltaTime();
			if (cos.y + 64 < 0) { iter.remove(); punkty++; }
			if (cos.overlaps(ty)) { fota = new Texture(Gdx.files.internal("textures/ded.png")); koniecRaz(); }
		}


		//zapisuje
		zapis.writeString(ach1 + "/" + ach2 + "/" + ach3 + "/" + ach4, false);
		System.out.println(ach1 + "/" + ach2 + "/" + ach3 + "/" + ach4);
	}

	public void draw() {
		//rozne kolory na rozne stany
		if (stan == State.START || stan == State.ACHIEVEMENT) ScreenUtils.clear(0.62f,0.28f,1.0f, 0);
		else if (stan == State.RUNNING) ScreenUtils.clear(0.92f,0.52f,0.95f, 0);
		else if (stan == State.KONIEC) ScreenUtils.clear(1.0f,0.3f,0.3f, 0);
		else if (stan == State.PAUSE) ScreenUtils.clear(0.92f,0.52f,0.95f, 0);

		kamerka.update();
		zadanie.setProjectionMatrix(kamerka.combined);

		zadanie.begin();

		//stan == RUNNING
		if (stan == State.RUNNING) {
			for (Rectangle cos: new Array.ArrayIterator<>(kilkaCos)) zadanie.draw(cosTekstura, cos.x, cos.y);
			zadanie.draw(fota, ty.x, ty.y);
		}

		//stan == START
		if (stan == State.START) wybieracz.draw(zadanie, "F1 - LATWY" + '\n' + "F2 - SREDNI" + '\n' + "F3 - TRUDNY" + '\n' + "F4 - ACHIEVEMENTY",
				hudKamerka.viewportWidth / 2.0f, hudKamerka.viewportHeight / 2.0f);

		//stan == ACHIEVEMENT
		if (stan == State.ACHIEVEMENT) {
			achievementWyjcie.draw(zadanie, "ESC - wyjscie", 0.0f, hudKamerka.viewportHeight - 21);
			if (ach1) { achTekst1.draw(zadanie, "150 PUNKTOW NA LATWYM", (hudKamerka.viewportWidth / 2.0f) - 500, (hudKamerka.viewportHeight / 2.0f) + 120); }
			if (ach2) { achTekst2.draw(zadanie, "175 PUNKTOW NA SREDNIM", (hudKamerka.viewportWidth / 2.0f) - 500, (hudKamerka.viewportHeight / 2.0f) + 60); }
			if (ach3) { achTekst3.draw(zadanie, "200 PUNKTOW NA TRUDNYM", (hudKamerka.viewportWidth / 2.0f) - 500, (hudKamerka.viewportHeight / 2.0f)); }
			if (ach4) { achTekst4.draw(zadanie, "WSZYSTKIE ACHIEVEMENTY + 200 NA TRUDNYM ZNOWU", (hudKamerka.viewportWidth / 2.0f) - 500, (hudKamerka.viewportHeight / 2.0f) - 60); }
		}

		//stan == KONIEC
		if (stan == State.KONIEC) {
			for (Rectangle cos: new Array.ArrayIterator<>(kilkaCos)) zadanie.draw(cosTekstura, cos.x, cos.y);
			zadanie.draw(fota, ty.x, ty.y);
			koniec.draw(zadanie, "UMARES", (hudKamerka.viewportWidth - 100) / 2.0f, hudKamerka.viewportHeight / 2.0f);
		}

		//stan == PAUSE
		if (stan == State.PAUSE) {
			for (Rectangle cos: new Array.ArrayIterator<>(kilkaCos)) zadanie.draw(cosTekstura, cos.x, cos.y);
			zadanie.draw(fota, ty.x, ty.y);
			zadanie.draw(przyciemnienie, 0, 0);
			pauza.draw(zadanie, "PAUZA", (hudKamerka.viewportWidth - 100) / 2.0f, hudKamerka.viewportHeight / 2.0f);
		}

		//zawsze
		fps.draw(zadanie, "" + Gdx.graphics.getFramesPerSecond(), 0.0f, hudKamerka.viewportHeight - 1);

		punkter.draw(zadanie, "PUNKTY: " + punkty, 0.0f, 20.0f);
		czaser.draw(zadanie, "CZAS: " + licznik, 0.0f, 40.0f);

		wersjaTekst.draw(zadanie, "v1.4", hudKamerka.viewportWidth - 34, 21);
		twurca.draw(zadanie, "copyright by rolnix", hudKamerka.viewportWidth - 161, 10);

		tryber.draw(zadanie, trybSlowo, hudKamerka.viewportWidth - 100, hudKamerka.viewportHeight - 1);

		zadanie.end();
	}

	public void menu() {
		if (Gdx.input.isKeyPressed(Input.Keys.F1)) { rozpocznij((byte) 0, "LATWY"); }
		if (Gdx.input.isKeyPressed(Input.Keys.F2)) { rozpocznij((byte) 1, "SREDNI"); }
		if (Gdx.input.isKeyPressed(Input.Keys.F3)) { rozpocznij((byte) 2, "TRUDNY"); }
		if (Gdx.input.isKeyPressed(Input.Keys.F4)) { achievementRaz(); }
	}

	public void rozpocznij(byte wybrane, String trybNapis) {
		poczatekCzas = TimeUtils.millis() / 1000;
		tryb = wybrane;
		trybSlowo = trybNapis;
		stan = State.RUNNING;
		wybor.play();
		tlo.play();
	}

	public void pauzaPetla() {
		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) stan = State.RUNNING;
	}

	public void achivementyPetla() {
		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			stan = State.START;
			wybor.play();
		}
	}
	public void achievementRaz() {
		stan = State.ACHIEVEMENT;
		wybor.play();
	}

	public void koniecRaz() {
		stan = State.KONIEC;
		tlo.stop();
		umarcie.play();
	}

	public void wczytaj() {
		String wczytane = zapis.readString();
		String[] gotowyOdczyt = wczytane.split("/");
		ach1 = Boolean.parseBoolean(gotowyOdczyt[0]);
		ach2 = Boolean.parseBoolean(gotowyOdczyt[1]);
		ach3 = Boolean.parseBoolean(gotowyOdczyt[2]);
		ach4 = Boolean.parseBoolean(gotowyOdczyt[3]);
	}

	@Override
	public void dispose () {
		zadanie.dispose();
		cosTekstura.dispose();
		fota.dispose();
		przyciemnienie.dispose();

		wybieracz.dispose();
		czaser.dispose();
		tryber.dispose();
		fps.dispose();
		punkter.dispose();
		wersjaTekst.dispose();
		twurca.dispose();
		koniec.dispose();
		pauza.dispose();
		achievementWyjcie.dispose();
		achTekst1.dispose();
		achTekst2.dispose();
		achTekst3.dispose();
		achTekst4.dispose();

		wybor.dispose();
		umarcie.dispose();
		tlo.dispose();
	}
}