# YahooNewsRssKotlinSample
RSS Reader Widget App Sample Project (Kotlin).

# アプリ概要

このアプリは、[YahooNewsRssJavaSample](https://github.com/LeoAndo/YahooNewsRssJavaSample)プロジェクトのKotlinバージョンになります。<br>
実装されてる機能は上記Java版プロジェクトと同じです。<br>
詳しいアプリの概要説明は以下の<br>
[README](https://github.com/LeoAndo/YahooNewsRssJavaSample/blob/main/README.md)を参照ください。<br>

# MAD Score

![summary](https://user-images.githubusercontent.com/16476224/150647888-ce67ec45-3c96-446a-b213-57bd868155a2.png)
![jetpack](https://user-images.githubusercontent.com/16476224/150647885-2708b656-0bd7-4dab-8666-961ed874a41b.png)
![kotlin](https://user-images.githubusercontent.com/16476224/150647886-1b19737f-b033-4bcd-bdf1-ebdbab30a166.png)
![studio](https://user-images.githubusercontent.com/16476224/150647887-4e36b2fb-bfa7-4879-b2a4-57d500ea73e3.png)

# Java -> Kotlin コンバート時の躓きポイント
`AppWidgetProvider`を継承したクラスを Java -> Kotlin コンバートしたところ、Android Studioのeditorがバグって、全体のコードがコンパイルエラーになったが、Android Stduioを再起動したら直った。<br>
発生バージョン<br>
<img width="642" alt="スクリーンショット 2021-06-05 20 30 03" src="https://user-images.githubusercontent.com/16476224/120890310-d0e5f180-c63c-11eb-9acb-77b89045d857.png">


## For Java
https://github.com/LeoAndo/YahooNewsRssJavaSample
