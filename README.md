# DS2022_30242_Saulea_Alessandro_Assignment_2

Smart Metering Device Simulator este o aplicatie maven, fisierul pom.xml continand dependintele aplicatiei. Aplicatia poate fi rulata dintr-un IDE precum Intellij cu un argument ce indica id-ul device-ului ce urmeaza sa primeasca masuratori si poate fi setat la sectiunea Run/Debug Configurations din Intellij asa cum este aratat in imaginea de mai jos. In momentul rularii, argumentul este introdus in fisierul de configurare config.properties.

![image](https://user-images.githubusercontent.com/74377027/207723383-3b927436-c399-4efa-b058-628ed78a4a42.png)

Pentru simularea mai multor senzori in acelasi timp, am creat o configuratie Compound in care pot rula 2 sau mai multe instante ale aplicatiei cu argumente diferite. Cand pornim configuratia Compound, toate instantele componente pornesc in acelasi timp.

![image](https://user-images.githubusercontent.com/74377027/207727623-9081aacc-6c9d-40eb-ac07-40250f918c40.png)

Daca la assignmentul anterior, am putut rula aplicatia REST API, baza de date si frontend-ul din IDE sau ca si containere Docker locale, pentru acest assigment am facut deploy la aceste aplicatii in Microsoft Azure.

In primul rand am descarcat o imagine de Postgres (docker pull postgres), am numit-o containerregistrysauleaalessandro30242/db (docker tag postgres containerregistrysauleaalessandro30242/db) si apoi am incarcat-o ca si repository in Azure (docker push containerregistrysauleaalessandro30242/db).

![image](https://user-images.githubusercontent.com/74377027/207739639-24697f6d-9671-44b1-a2cb-2a07baf0dda8.png)

Pentru a putea rula serviciile CI/CD, este nevoie de un Agent responsabil pentru toate instructiunile. Am creat un agent cu numele local si l-am folosit pentru intregul proces de deployment. Agentul este folosit pentru construirea imaginii de docker pentru codul de pe GitHub si de a o incarca in Docker Registry, iar apoi sa faca un deployment al acestei imagini in Docker on Azure.

![image](https://user-images.githubusercontent.com/74377027/207740501-f8fae1dd-f1c5-4bc2-874b-6b618a0ca3ec.png)

Pentru functionarea agentului, am folosit o masina virtuala Ubuntu ce include Agentul respectiv, noi trebuind doar sa configuram credentialele de acces. Am pornit agentul cu comanda ./run.sh in masina virtuala. Am instalat serviciul cu comenzile sudo ./svc.sh install and sudo ./svc.sh start.

Apoi in Azure Devops, am creat un CI/CD pipeline pe repository-ul de la assignment-ul 1, care mi-a creat un fisier azure-pipelines.yml ce se ocupa de configurarea celor 2 repository-uri pe backend si frontend.

![image](https://user-images.githubusercontent.com/74377027/207743287-847a4769-46ba-4ec9-8286-1206ef09375a.png)

Dupa ce pipeline-ul si-a terminat executia, am inceput efeciv procesul de deployment, prin crearea unui release care, cu ajutorului agentului creat anterior, descarca artifactele aplicatiei, realizeaza comanda de Docker Compose si creeaza containerele corespunzatoare repository-urilor configurate anterior.

![image](https://user-images.githubusercontent.com/74377027/207744190-943907dd-86dc-40c4-83ed-25b276c3076c.png)

Containerele obtinute prin aceasta ultima procedura arata astfel: 

![image](https://user-images.githubusercontent.com/74377027/207744463-c5a48879-351a-4eda-a140-60e74cefc23e.png)

