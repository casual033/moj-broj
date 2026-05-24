# Moj Broj (Android MVP)

Mobilna igra inspirisana igrom "Moj broj", napravljena kao Kotlin/Jetpack Compose aplikacija sa odvojenim `game-core` modulom.

## Šta trenutno postoji

- Android aplikacija (`app-android`) u Jetpack Compose
- Čist Kotlin game engine (`game-core`) bez Android zavisnosti
- Parser/evaluator izraza sa pravilima validacije
- Solver za najbolje moguće rešenje
- Režimi igre:
  - `Standard`
  - `Za decu`
- Lokalna statistika (DataStore)
- Osnovni branding (launcher ikonica, naziv aplikacije)

## Pravila igre

### Standard
- Brojevi: `4 mala (1-9) + 1 srednji (10/15/20) + 1 veliki (25/50/75/100)`
- Cilj: `100-999`

### Za decu
- Brojevi: `3 mala (1-9) + 1 srednji (10/15/20)`
- Cilj: `10-100`

### Operacije i validacija
- Dozvoljeno: `+`, `-`, `*`, `/`, `(`, `)`
- Svaki ponuđeni broj može da se koristi najviše jednom
- Deljenje mora biti celobrojno
- Negativni međurezultati nisu dozvoljeni

## Struktura projekta

```text
moj-broj/
├── app-android/      # Android UI, ViewModel, DataStore
├── game-core/        # Generator, evaluator, validator, solver
├── settings.gradle.kts
└── build.gradle.kts
```

## Pokretanje lokalno

### 1) Build

```bash
gradle :app-android:assembleDebug
```

### 2) Testovi

```bash
gradle :game-core:test
```

### 3) Install na uređaj

```bash
gradle :app-android:installDebug
adb shell am start -n com.mojbroj.app/.MainActivity
```

## Korisne komande

```bash
# svi testovi
gradle test

# lint (android)
gradle :app-android:lintDebug
```

## Napomena

Ovaj repo je MVP osnova. Sledeći koraci su dalji UX/UI polish, balans težine i proširenja funkcionalnosti (daily challenge, leaderboard, itd).
