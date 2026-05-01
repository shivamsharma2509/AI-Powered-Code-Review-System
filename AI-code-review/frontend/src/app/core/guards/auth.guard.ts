import { CanActivateFn } from '@angular/router';
import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

export const authGuard: CanActivateFn = () => {

  const platformId = inject(PLATFORM_ID);

  // SSR SAFE CHECK
  if (!isPlatformBrowser(platformId)) {
    return false;
  }

  const token = localStorage.getItem('token');

  if (token) {
    return true;
  }

  window.location.href = '/login';
  return false;
};
