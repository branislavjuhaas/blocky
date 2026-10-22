# Blocky

**Blocky** je jednoduchý nástroj pre príkazový riadok, ktorý vytlačí účtenku, akú dostanete v samoobsluhe, supermarkete alebo trafike. Účtenku vypíše do konzoly, zároveň ju uloží do textového súboru a na konci vypíše cestu k nemu.

Aktuálna verzia: **2.1.0** (verzujeme podľa [Semantic Versioning](https://semver.org/lang/sk/)).

## Autori

- Adam Mistrík
- Branislav Juhás
- Šimon Štefánik
- Tetiana Tkachenko

## Spustenie (bez vývojového prostredia)

```bash
cd blocky
java -jar target/blocky-2.1.0.jar                      # použije ./store.json
java -jar target/blocky-2.1.0.jar iny-obchod.json
java -jar target/blocky-2.1.0.jar --version
java -jar target/blocky-2.1.0.jar --help
```

## Program sa postupne pýta na položky nákupu:

```
Názov položky 1: Rožok
  Množstvo [1]: 3
  Cena za kus (€): 0,15
  + Rožok = 0,45 €
Názov položky 2:                ← prázdny názov ukončí zadávanie
Spolu na úhradu: 0,45 €
Zaplatené (€) [0,45]: 1
```

- Čísla sa dajú písať s desatinnou čiarkou aj bodkou (`0,15` aj `0.15`). Množstvo môže byť desatinné (napr. `0,5` kg).
- Prázdne množstvo znamená 1 kus a prázdna platba znamená, že zákazník platí presne.
- Keď zadáte neplatné číslo, program sa opýta znova.

Výsledná účtenka:

```
========================================
            Potraviny Blocky
     Hlavná 123, 811 01 Bratislava
     IČO: 12345678  DIČ: 2020123456
         Tel.: +421 900 123 456
========================================
Účtenka č.: 20261022-182004
Dátum:      22.10.2026 18:20:04
----------------------------------------
Rožok
  3 x 0,15                          0,45
Mlieko
  1 x 1,09                          1,09
Banány
  1,25 x 1,49                       1,86
----------------------------------------
SPOLU                             3,40 €
----------------------------------------
Zaplatené                         5,00 €
Vrátené                           1,60 €
----------------------------------------
Základ DPH 23 %                   2,76 €
DPH 23 %                          0,64 €
========================================
 Ďakujeme za nákup a tešíme sa na Vás!
             Blocky v2.1.0

Účtenka bola uložená do súboru: /…/blocky/uctenky/uctenka-20261022-182004.txt
```

## Konfigurácia obchodu

Údaje, ktoré sa na účtenke opakujú, sú v súbore `blocky/store.json` (JSON, UTF-8):

```json
{
  "store": {
    "name": "Potraviny Blocky",
    "address": "Hlavná 123, 811 01 Bratislava",
    "ico": "12345678",
    "dic": "2020123456",
    "phone": "+421 900 123 456"
  },
  "receipt": {
    "footer": "Ďakujeme za nákup a tešíme sa na Vás!",
    "currency": "€",
    "width": 40
  },
  "vatRate": 23,
  "outputDir": "uctenky"
}
```

| Kľúč               | Typ   | Význam                                 | Predvolená hodnota |
|--------------------|-------|----------------------------------------|--------------------|
| `store.name`       | text  | názov obchodu (**povinné**)            |                    |
| `store.address`    | text  | adresa                                 | prázdne            |
| `store.ico`        | text  | IČO                                    | prázdne            |
| `store.dic`        | text  | DIČ                                    | prázdne            |
| `store.phone`      | text  | telefón                                | prázdne            |
| `receipt.footer`   | text  | text v päte účtenky                    | prázdne            |
| `receipt.currency` | text  | mena                                   | `€`                |
| `receipt.width`    | číslo | šírka účtenky v znakoch (min. 30)      | `40`               |
| `vatRate`          | číslo | sadzba DPH v %, ceny sa zadávajú s DPH | `23`               |
| `outputDir`        | text  | priečinok, kam sa ukladajú účtenky     | `uctenky`          |

Nepovinné kľúče sa dajú vynechať. JSON sa číta striktne: preklep (chýbajúce úvodzovky, čiarka navyše) alebo zlý typ hodnoty program oznámi a skončí.

Ak v priečinku, z ktorého program spúšťate, nie je žiadny `store.json`, použije sa predvolená konfigurácia pribalená v JAR súbore.

JSON číta knižnica Gson. Maven ju pri `package` zabalí priamo do JAR (maven-shade-plugin), takže na spustenie stačí len Java.

## Testy

```bash
cd blocky
mvn test
```

Unit testy (JUnit 5) pokrývajú načítanie konfigurácie, výpočty cien a DPH, formátovanie čísel a účtenky, zadávanie od používateľa, ukladanie do súboru aj celý beh programu.
