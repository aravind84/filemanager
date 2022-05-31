app_ver := 1.0

GO_PATH:=$(shell go env GOPATH)
PATH:=${PATH}:${GO_PATH}/bin
export PATH
all: kind_install k8s_creation build_app docker_build_push deploy_monitoring deploy_app

kind_install:
	go install sigs.k8s.io/kind@v0.14.0

k8s_creation:
	./kind-cluster.sh

build_app:
	./gradlew -PprojVersion=$(app_ver) clean build

docker_build_push:
	docker build -t filemanager:$(app_ver) --build-arg appVersion=$(app_ver) . 
	docker tag filemanager:$(app_ver) localhost:5001/filemanager:$(app_ver)
	docker push localhost:5001/filemanager:$(app_ver)
	
deploy_monitoring:
	helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
	helm repo update
	pwd
	helm -n monitoring install prometheus prometheus-community/kube-prometheus-stack --create-namespace -f helm_chart/custom-prometheus-values.yaml --debug

deploy_app:
	helm -n monitoring install filemanager helm_chart/ --create-namespace --debug

access_service:
	kubectl -n monitoring port-forward svc/prometheus-grafana 9080:80

clean:
	helm -n monitoring uninstall filemanager
	helm -n monitoring uninstall prometheus
	${GO_PATH}/bin/kind delete cluster
