# Blocky

**Blocky** je jednoduchý nástroj pro příkazovou řádku, který vytiskne účtenku, jakou dostanete v samoobsluze, supermarketu nebo trafice. Účtenku vypíše do konzole a zároveň ji uloží do textového souboru. Cestu k souboru vypíše na konec.

Aktuální verze: **1.0.0** (verzujeme podle [Semantic Versioning](https://semver.org/lang/cs/)).

## Autoři

- Adam Mistrík
- _doplňte jména dalších členů týmu_

## Požadavky

- Java 25 (JDK)
- Maven 3.9+ (nebo Maven přibalený v IntelliJ IDEA)

## Sestavení

```bash
cd blocky
mvn package
```

Maven přeloží kód, spustí unit testy a vytvoří spustitelný soubor `blocky/target/blocky-1.0.0.jar`.

## Spuštění (bez vývojového prostředí)

```bash
cd blocky
java -jar target/blocky-1.0.0.jar                      # použije ./store.properties
java -jar target/blocky-1.0.0.jar jiny-obchod.properties
java -jar target/blocky-1.0.0.jar --version
java -jar target/blocky-1.0.0.jar --help
```

Program se postupně ptá na položky nákupu:

```
Název položky 1: Rohlík
  Množství [1]: 3
  Cena za kus (Kč): 2,50
  + Rohlík = 7,50 Kč
Název položky 2:                ← prázdný název ukončí zadávání
Celkem k úhradě: 7,50 Kč
Zaplaceno (Kč) [7,50]: 10
```

- Čísla lze psát s desetinnou čárkou i tečkou (`2,50` i `2.50`), množství může být desetinné (např. `0,5` kg).
- Prázdné množství znamená 1 kus a prázdná platba znamená, že zákazník platí přesně.
- Když zadáte neplatné číslo, program se zeptá znovu.

Výsledná účtenka:

```
========================================
            Potraviny Blocky
       Hlavní 123, 110 00 Praha 1
     IČO: 12345678  DIČ: CZ12345678
         Tel.: +420 123 456 789
========================================
Účtenka č.: 20261022-180538
Datum:      22.10.2026 18:05:38
----------------------------------------
Rohlík
  3 x 2,50                          7,50
Mléko polotučné 1l
  1 x 24,90                        24,90
Banány
  1,25 x 39,90                     49,88
----------------------------------------
CELKEM                          82,28 Kč
----------------------------------------
Zaplaceno                      100,00 Kč
Vráceno                         17,72 Kč
----------------------------------------
Základ DPH 21 %                 68,00 Kč
DPH 21 %                        14,28 Kč
========================================
 Děkujeme za nákup a těšíme se na Vás!
             Blocky v1.0.0

Účtenka byla uložena do souboru: /…/blocky/uctenky/uctenka-20261022-180538.txt
```

## Konfigurace obchodu

Údaje, které se na účtence opakují, jsou v souboru `blocky/store.properties` (UTF-8):

| Klíč               | Význam                                   | Výchozí hodnota |
|--------------------|------------------------------------------|-----------------|
| `store.name`       | název obchodu (**povinné**)              |                 |
| `store.address`    | adresa                                   | prázdné         |
| `store.ico`        | IČO                                      | prázdné         |
| `store.dic`        | DIČ                                      | prázdné         |
| `store.phone`      | telefon                                  | prázdné         |
| `receipt.footer`   | text v patičce                           | prázdné         |
| `receipt.currency` | měna                                     | `Kč`            |
| `receipt.width`    | šířka účtenky ve znacích (min. 30)       | `40`            |
| `vat.rate`         | sazba DPH v %, ceny se zadávají s DPH    | `21`            |
| `output.dir`       | složka, kam se ukládají účtenky          | `uctenky`       |

Pokud ve složce, odkud program spouštíte, žádný `store.properties` není, použije se výchozí konfigurace přibalená v JAR souboru.

## Testy

```bash
cd blocky
mvn test
```

Unit testy (JUnit 5) pokrývají načítání konfigurace, výpočty cen a DPH, formátování čísel a účtenky, zadávání od uživatele, ukládání do souboru i celý běh programu.

## Jak vydat další verzi

1. Udělejte změny v kódu a doplňte k nim testy.
2. Zvyšte verzi v `blocky/pom.xml` (`<version>`) podle sémantického verzování:
   - **PATCH** (`1.0.0` → `1.0.1`): oprava chyby,
   - **MINOR** (`1.0.0` → `1.1.0`): nová funkce, která nic nerozbije,
   - **MAJOR** (`1.0.0` → `2.0.0`): nekompatibilní změna (např. formát konfigurace).

   Verze se automaticky propíše do programu (`--version`) i do patičky účtenky.
3. Sestavte a otestujte: `mvn clean package`.
4. Commitněte, označte verzi tagem a pushněte:
   ```bash
   git commit -am "Release 1.1.0"
   git tag v1.1.0
   git push && git push --tags
   ```
5. Volitelně nahrajte `target/blocky-1.1.0.jar` na GitHub jako release.

## Struktura projektu

```
blocky/
├── pom.xml
├── store.properties                  konfigurace obchodu
└── src/
    ├── main/java/cz/blocky/
    │   ├── Main.java                 vstupní bod, spojuje vše dohromady
    │   ├── AppInfo.java              název a verze aplikace
    │   ├── StoreInfo.java            údaje o obchodě
    │   ├── StoreInfoLoader.java      načtení konfigurace ze souboru
    │   ├── Item.java                 položka nákupu
    │   ├── Receipt.java              účtenka, výpočet součtu, DPH a vrácení
    │   ├── ReceiptInput.java         zadávání položek z konzole
    │   ├── ReceiptFormatter.java     textová podoba účtenky
    │   ├── ReceiptWriter.java        uložení do souboru
    │   └── Money.java                formátování a čtení čísel
    └── test/java/cz/blocky/          unit testy
```
