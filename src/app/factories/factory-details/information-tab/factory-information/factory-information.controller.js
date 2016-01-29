/*
 * Copyright (c) 2015-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 */
'use strict';

/**
 * Controller for a factory information.
 * @author Oleksii Orel
 */
export class FactoryInformationCtrl {

  /**
   * Default constructor that is using resource injection
   * @ngInject for Dependency injection
   */
  constructor($scope, codenvyAPI, cheNotification) {
    this.codenvyAPI = codenvyAPI;
    this.cheNotification = cheNotification;

    let ctrl = this;

    $scope.$watch('factoryInformationCtrl.factory.originFactory', function (newOriginFactory) {
      if(!newOriginFactory){
        return;
      }
      ctrl.copyOriginFactory = angular.copy(newOriginFactory);
    });
  }

  //Set factory content.
  setFactory(factory) {

    let promise = this.codenvyAPI.getFactory().setFactory(factory);

    promise.then(() => {
      this.factory.originFactory = factory;
      this.cheNotification.showInfo('Factory information successfully updated.');
    }, (error) => {
      this.cheNotification.showError(error.data.message ? error.data.message : 'Update factory failed.');
      console.log('error', error);
    });
  }
}
