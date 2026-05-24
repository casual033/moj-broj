# Moj Broj – Specifikacija Mobilne Igre (MVP + Roadmap)

## 1. Cilj aplikacije

Napraviti mobilnu igru "Moj broj" gde korisnik od ponuđenih brojeva i osnovnih matematičkih operacija pokušava da dobije zadati broj.

Glavni cilj MVP-a:

> Igrač dobije zadatak, unese izraz, aplikacija proveri rezultat i pokaže najbolje rešenje.

---

## 2. MVP funkcionalnosti

MVP scope (obavezno):
- offline single-player
- jedna partija = jedan target + 6 brojeva + 60s tajmer
- ručni unos izraza preko UI dugmadi
- validacija izraza po pravilima igre
- prikaz korisničkog i najboljeg (solver) rezultata
- lokalna statistika

Van scope-a za MVP:
- login/nalog
- online leaderboard
- multiplayer
- push notifikacije
- cloud sync

### Ekrani

### 1. Početni ekran
- Nova igra
- Pravila
- Podešavanja

### 2. Igra
- ciljni broj
- 6 ponuđenih brojeva
- dugmad za + - * / ( )
- prikaz izraza
- tajmer (npr. 60 sekundi)
- dugme "Proveri"

### 3. Rezultat
- korisnikov izraz
- korisnikov rezultat
- razlika od cilja
- najbolje moguće rešenje
- status: "Tačno", "Najbliže", "Nevalidan unos"
- dugme "Nova igra"

---

## 3. Pravila igre

Brojevi:
- mali brojevi: 1–9
- veliki brojevi: 25, 50, 75, 100
- ukupno 6 brojeva
- ciljni broj: 100–999

Operacije:
- sabiranje (+)
- oduzimanje (-)
- množenje (*)
- deljenje (/)

Validacija:
- svaki ponuđeni broj može da se koristi najviše jednom
- deljenje mora da bude celobrojno
- međurezultati ne treba da budu negativni
- rezultat može biti tačan ili najbliži

Napomena za MVP:
- korisnik bira koliko velikih brojeva želi (0-4), ostalo su mali brojevi
- ako korisnik ne bira, default je 2 velika + 4 mala

### Tok jedne partije (state machine)
1. `Idle` (čekanje na "Nova igra")
2. `InProgress` (tajmer aktivan, unos izraza)
3. `Submitted` (korisnik klikne "Proveri" pre isteka)
4. `TimeUp` (isteklo vreme bez potvrde)
5. `Evaluated` (izračunat korisnički rezultat + solver rezultat)
6. `ResultShown` (prikaz rezimea i opcija za novu partiju)

Pravila prelaza:
- "Proveri" je dozvoljeno samo u `InProgress`
- kada tajmer istekne, unos se zaključava
- iz `ResultShown` jedini prelaz je na novu partiju (`Idle`)

---

## 4. Tehnička arhitektura

Predlog arhitekture:

```text
MojBrojApp
│
├── app-android
│   └── Jetpack Compose UI
│
├── game-core
│   ├── GameGenerator
│   ├── ExpressionParser
│   ├── ExpressionValidator
│   ├── Solver
│   └── ScoringService
│
└── data
    ├── LocalStatsRepository
    └── PreferencesRepository
```

Najvažnije:

- game-core mora biti čist Kotlin modul
- bez Android zavisnosti
- omogućava budući Android / iOS / Web reuse
- parser/validator/solver API treba da bude determinističan (isti input -> isti output)
- sve game-core funkcije moraju imati unit testove

---

## 5. Core modeli

Primer:

```kotlin
data class GameRound(
    val target: Int,
    val numbers: List<Int>
)

data class SubmittedSolution(
    val expression: String,
    val result: Int?,
    val isValid: Boolean,
    val distance: Int
)

data class SolverResult(
    val expression: String,
    val result: Int,
    val distance: Int
)

enum class EvaluationStatus {
    EXACT,
    CLOSEST,
    INVALID
}
```

---

## 6. Faze razvoja

## Faza 1 — Core engine

Cilj:

Sve funkcioniše bez UI-a.

Potrebno:
- generisanje brojeva
- generisanje target broja
- parser izraza
- validacija korišćenih brojeva
- računanje rezultata
- scoring

Definition of Done:
- svi core use-case testovi prolaze
- parser pokriva zagrade i prioritete operacija
- validacija hvata: nepostojeći broj, duplu upotrebu, necelobrojno deljenje
- rezultat evaluacije vraća jasan status (`EXACT`/`CLOSEST`/`INVALID`)

Primer:

```text
Unos: (25 + 75) * 8
Target: 799
Rezultat: 800
Razlika: 1
Validno: da
```

---

## Faza 2 — Solver

Solver treba da pronađe najbolje moguće rešenje.

Predlog:
- brute force
- backtracking
- expression tree pristup

Solver vraća:
- tačno rešenje ako postoji
- najbliže rešenje ako ne postoji

MVP ograničenja performansi:
- solver mora da vrati rezultat za standardni set od 6 brojeva za < 150ms na prosečnom mobilnom uređaju
- u slučaju prekoračenja vremenskog budžeta, vratiti najbolje do tada nađeno rešenje

---

## Faza 3 — Android UI

Predlog tehnologija:
- Kotlin
- Jetpack Compose
- ViewModel
- StateFlow
- Hilt (opciono)
- Room (opciono)

Ekrani:
- HomeScreen
- GameScreen
- ResultScreen
- SettingsScreen

Definition of Done:
- kompletan game flow radi bez rušenja
- konfiguracija broja velikih brojeva dostupna iz `SettingsScreen`
- prikaz validacionih grešaka je jasan i čitljiv korisniku

---

## Faza 4 — Statistika

Čuvati:
- broj partija
- broj tačnih rešenja
- prosečnu razliku od cilja
- najbolji streak
- prosečno vreme rešavanja

Tehnički minimum:
- lokalno čuvanje preko `DataStore` ili `Room` (jedno od ta dva u MVP-u)
- statistika se ažurira nakon svake završene partije

---

## Faza 5 — Dodatne funkcije

Kasnije:
- dnevni izazov
- težine
- hint sistem
- leaderboard
- multiplayer
- deljenje rezultata
- rešivi-only mod

---

## 7. Predloženi redosled rada

1. Definisati pravila
2. Napraviti GameRound generator
3. Napraviti solver
4. Napraviti validator izraza
5. Dodati unit testove
6. Napraviti Compose UI
7. Dodati statistiku
8. Ispeglati UX

---

## 8. Prvi backlog – Playable MVP

Milestone 1:

- [ ] Kreirati Kotlin projekat
- [ ] Dodati game-core modul
- [ ] Napraviti GameRound
- [ ] Napraviti generator brojeva
- [ ] Napraviti target generator
- [ ] Napraviti solver
- [ ] Napraviti parser izraza
- [ ] Validirati da se brojevi koriste jednom
- [ ] Dodati osnovni Compose ekran
- [ ] Dodati tajmer
- [ ] Dodati rezultat ekran

---

## 9. QA i test plan (MVP)

Unit testovi (`game-core`):
- generator daje validne setove brojeva (dimenzija, opseg, odnos malih/velikih)
- parser ispravno rešava prioritet operacija i zagrade
- validator odbija duplu upotrebu broja i nedozvoljeno deljenje
- evaluator vraća tačan rezultat i status
- solver nalazi `distance = 0` kada tačno rešenje postoji

Integracioni/UI testovi:
- start nove igre -> unos izraza -> provera -> rezultat ekran
- istek tajmera zaključava unos i prikazuje rezultat
- "Nova igra" resetuje stanje i pokreće novu rundu

Manual smoke checklist:
- aplikacija se pokreće bez interneta
- rotacija ekrana (ako je podržana) ne lomi partiju
- brz povratak iz pozadine ne resetuje aktivnu partiju

---

## 10. Definisane odluke (da nema blokera)

Ako nešto nije eksplicitno zadato, za MVP važi:
- trajanje partije: 60 sekundi
- default raspodela brojeva: 2 velika + 4 mala
- cilj je minimizacija apsolutne razlike od target-a
- bez negativnih međurezultata i bez decimalnih rezultata
- format unosa je infix izraz sa podrškom za zagrade

---

## Zaključak

Preporuka:

Prvo napraviti kvalitetan core engine i solver. Kada je game logika stabilna i dobro testirana, UI i dodatne funkcionalnosti postaju mnogo jednostavnije za razvoj i održavanje.
