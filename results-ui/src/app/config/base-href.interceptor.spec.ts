import { TestBed } from '@angular/core/testing';

import { BaseHrefInterceptor } from './base-href.interceptor';

describe('BaseHrefInterceptor', () => {
  beforeEach(() => TestBed.configureTestingModule({
    providers: [
      BaseHrefInterceptor
      ]
  }));

  it('should be created', () => {
    const interceptor: BaseHrefInterceptor = TestBed.inject(BaseHrefInterceptor);
    expect(interceptor).toBeTruthy();
  });
});
