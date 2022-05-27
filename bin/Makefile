app_ver := 1.0

GO_PATH:=$(shell go env GOPATH)
PATH:=${PATH}:${GO_PATH}/bin
export PATH
all: kind_install build_app docker_build_push deploy_monitoring deploy_app

kind_install:
	./kind-cluster.sh

build_app:
	./gradlew clean build

docker_build_push:
	docker build -t filemanager:$(app_ver) . 
	docker tag filemanager:$(app_ver) localhost:5001/filemanager:$(app_ver)
	docker push localhost:5001/filemanager:$(app_ver)
	
deploy_monitoring:
	helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
	helm repo update
	helm -n monitoring install prometheus prometheus-community/kube-prometheus-stack --create-namespace --debug

deploy_app:
	helm -n filemanager install filemanager helm_chart/ --create-namespace --debug

access_service:
	kubectl -n monitoring port-forward svc/prometheus-grafana 9080:80

clean:
	helm -n filemanager uninstall filemanager
	helm -n monitoring uninstall prometheus
	kind delete cluster

test:
	echo ${GO_PATH}
	export PATH=${PATH}:${GO_PATH}/bin