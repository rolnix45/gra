package com.rolnix.gra;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
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

	public enum State{ RUNNING, PAUSE, START }
	State stan = State.START;

	private OrthographicCamera kamerka;
	private OrthographicCamera hudKamerka;

	private SpriteBatch zadanie;
	private Rectangle ty;
	private Texture fota;
	private Texture cosTekstura;
	private Array<Rectangle> kilkaCos;

	private BitmapFont fps;
	private BitmapFont koniec;
	private BitmapFont czaser;
	private BitmapFont punkter;
	private BitmapFont tryber;
	private BitmapFont wybieracz;
	private BitmapFont wersjaTekst;
	private BitmapFont twurca;
	private String trybSlowo;

	private Sound wybor;
	private Sound umarcie;
	private Music tlo;

	private long tak;
	private long czasOdOstatnieCos;
	private long poczatekCzas;
	private long licznik;
	private long punkty;
	private int mnoznik;
	private int szypkosc;
	private boolean czyKoniec;
	private static byte tryb;

	@Override
	public void create () {
		fota = new Texture(Gdx.files.internal("textures/fota.png"));
		cosTekstura = new Texture(Gdx.files.internal("textures/cos.png"));

		wybieracz = new BitmapFont(Gdx.files.internal("font/dosvga437.fnt"));
		czaser = new BitmapFont(Gdx.files.internal("font/dosvga437.fnt"));
		tryber = new BitmapFont(Gdx.files.internal("font/dosvga437.fnt"));
		fps = new BitmapFont(Gdx.files.internal("font/dosvga437.fnt"));
		punkter = new BitmapFont(Gdx.files.internal("font/dosvga437.fnt"));
		wersjaTekst = new BitmapFont(Gdx.files.internal("font/dosvga437.fnt"));
		twurca = new BitmapFont(Gdx.files.internal("font/dosvga437.fnt"));
		koniec = new BitmapFont(Gdx.files.internal("font/dosvga437.fnt"));

		twurca.getData().setScale(0.5f);
		wersjaTekst.getData().setScale(0.5f);
		koniec.getData().setScale(3.0f);

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
			case PAUSE:
				break;
		} draw();
	}

	public void update() {
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) ty.x -= 200 * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) ty.x += 200 * Gdx.graphics.getDeltaTime();

		if (ty.x < 0) ty.x = 0;
		if (ty.x > 1366 - 80) ty.x = 1366 - 80;

		if (czyKoniec) { stan = State.PAUSE; umarcie.play(); }

		if (TimeUtils.nanoTime() - czasOdOstatnieCos > tak) spawnCos();
		
		tak -= mnoznik;
		switch (tryb) {
			//latwy
			case 0:
				szypkosc = 200;
				if (tak < 750000000) mnoznik = 200000;
				if (tak < 333333333) { mnoznik = 300000; szypkosc = 215; }
				if (tak < 250000000) { mnoznik = 0;  szypkosc = 225; }
				break;

			//sredni
			case 1:
				szypkosc = 225;
				if (tak < 850000000) mnoznik = 200000;
				if (tak < 666666667) { mnoznik = 300000; szypkosc = 240; }
				if (tak < 400000000) { mnoznik = 400000; szypkosc = 250; }
				if (tak < 150000000) mnoznik = 0;
				break;

			//trudny
			case 2:
				szypkosc = 250;
				if (tak < 850000000) mnoznik = 250000;
				if (tak < 666666667) { mnoznik = 350000; szypkosc = 265; }
				if (tak < 400000000) { mnoznik = 450000; szypkosc = 275;}
				if (tak < 100000000) mnoznik = 0;
				break;
		}

		long minietyCzas = TimeUtils.millis() / 1000;
		licznik = minietyCzas - poczatekCzas;

		for (Iterator<Rectangle> iter = new Array.ArrayIterator<>(kilkaCos).iterator(); iter.hasNext();) {
			Rectangle cos = iter.next();
			cos.y -= szypkosc * Gdx.graphics.getDeltaTime();
			if (cos.y + 64 < 0) { iter.remove(); punkty++; }
			if (cos.overlaps(ty)) { fota = new Texture(Gdx.files.internal("textures/ded.png")); czyKoniec = true; }
		}
	}

	public void draw() {
		if (stan == State.START) ScreenUtils.clear(0.62f,0.28f,1.0f, 0);
		else if (czyKoniec) ScreenUtils.clear(1.0f,0.3f,0.3f, 0);
		else ScreenUtils.clear(0.92f,0.52f,0.95f, 0);

		kamerka.update();
		zadanie.setProjectionMatrix(kamerka.combined);

		zadanie.begin();
		if (stan != State.START) zadanie.draw(fota, ty.x, ty.y);

		for (Rectangle cos: new Array.ArrayIterator<>(kilkaCos)) zadanie.draw(cosTekstura, cos.x, cos.y);

		fps.draw(zadanie, "" + Gdx.graphics.getFramesPerSecond(), 0.0f, hudKamerka.viewportHeight - 1);
		wersjaTekst.draw(zadanie, "v1.3.1", hudKamerka.viewportWidth - 51, 21);
		twurca.draw(zadanie, "made by rolnix oczywiscie", hudKamerka.viewportWidth - 212, 10);

		punkter.draw(zadanie, "PUNKTY: " + punkty, 0.0f, 20.0f);
		czaser.draw(zadanie, "CZAS: " + licznik, 0.0f, 40.0f);

		if (czyKoniec) koniec.draw(zadanie, "UMARES", (hudKamerka.viewportWidth - 100) / 2.0f, hudKamerka.viewportHeight / 2.0f);

		tryber.draw(zadanie, trybSlowo, hudKamerka.viewportWidth - 100, hudKamerka.viewportHeight - 1);
		if (stan == State.START) wybieracz.draw(zadanie, "F1 - LATWY" + '\n' + "F2 - SREDNI" + '\n' + "F3 - TRUDNY",
				hudKamerka.viewportWidth / 2.0f, hudKamerka.viewportHeight / 2.0f);

		zadanie.end();
	}

	public void menu() {
		if (Gdx.input.isKeyPressed(Input.Keys.F1) && stan == State.START) { tryb = 0; trybSlowo = "LATWY"; stan = State.RUNNING;
			wybor.play(); tlo.play(); poczatekCzas = TimeUtils.millis() / 1000; }
		if (Gdx.input.isKeyPressed(Input.Keys.F2) && stan == State.START) { tryb = 1; trybSlowo = "SREDNI"; stan = State.RUNNING;
			wybor.play(); tlo.play(); poczatekCzas = TimeUtils.millis() / 1000; }
		if (Gdx.input.isKeyPressed(Input.Keys.F3) && stan == State.START) { tryb = 2; trybSlowo = "TRUDNY"; stan = State.RUNNING;
			wybor.play(); tlo.play(); poczatekCzas = TimeUtils.millis() / 1000; }
	}

	@Override
	public void dispose () {
		cosTekstura.dispose(); fota.dispose(); fps.dispose();
		koniec.dispose(); czaser.dispose(); punkter.dispose();
		tryber.dispose(); wybieracz.dispose(); zadanie.dispose();
	}
}