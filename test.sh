#!/bin/bash
currencies=("AUD" "CAD" "CHF" "EUR" "GBP" "JPY" "NZD" "SGD" "USD")
for i in {1..10000}
do
  rand1=${currencies[$RANDOM % ${#currencies[@]} ]}
  rand2=${currencies[$RANDOM % ${#currencies[@]} ]}
  echo "Attempt $i:"
  curl http://localhost:8080/api/exchange-rates?fromCurrency=${rand1}\&toCurrency=${rand2}
  echo " "
done
