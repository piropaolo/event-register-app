# event-register-app

Serwis, który rejestruje liczbę unikalnych zdarzeń od użytkowników.

## Budowanie projektu

```bash
mvn clean compile package
```

## Uruchomienie aplikacji

```bash
mvn spring-boot:run
```

## Uruchomienie testów jednostkowych

```bash
mvn test
```

## Dodatkowe uwagi

Obługa dużej liczby równoległych zapisów i odczytów jest zapewniona poprzez wykorzystanie *ConcurrentHashMap* do przechowywania zdarzeń. W testach jednostkowych sprawdzono działanie serwisu w przypadku, gdy do operacji na mapie wykorzystwane są 32 wątki, a liczba różnych typów zdarzeń waha się od 5 do 10. Niemniej jednak, uwzględniono możliwość ustawienia innych paremtrów dla mapy, w tym poziomu współbieżności.

W kwestii usuwania zdarzeń z pamięci postawiono na adnotację *@Scheduled*, i ustawiono czyszczenie starych zdarzeń co minutę.

W folderze *demo* zamieszczono przykładowe skrypty testujące endpointy z pomocą *curla*.

## Autor

Paweł Kamiński
